package com.easyaccounting.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.easyaccounting.common.exception.BusinessException;
import com.easyaccounting.common.exception.ErrorCode;
import com.easyaccounting.common.util.JwtUtil;
import com.easyaccounting.entity.User;
import com.easyaccounting.model.dto.LoginRequest;
import com.easyaccounting.model.dto.RefreshTokenRequest;
import com.easyaccounting.model.dto.RegisterRequest;
import com.easyaccounting.model.dto.ResetPasswordRequest;
import com.easyaccounting.model.dto.ResetPasswordTokenRequest;
import com.easyaccounting.model.enums.SmsType;
import com.easyaccounting.model.vo.LoginResponse;
import com.easyaccounting.model.vo.UserVO;
import com.easyaccounting.service.IAuthService;
import com.easyaccounting.service.ISmsService;
import com.easyaccounting.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserService userService;
    private final ISmsService smsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for account: {}", request.getAccount());
        
        // 1. 查找用户
        User user = userService.getByPhone(request.getAccount());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 2. 检查锁定状态
        if (user.getLockoutTime() != null && user.getLockoutTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "账号已锁定，请稍后再试");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }

        // 4. 登录成功处理
        handleSuccessfulLogin(user);

        // 5. 生成 Token
        String accessToken = jwtUtil.generateToken(user.getId(), user.getPhone());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 6. 构建响应
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        // 脱敏处理
        userVO.setPhone(StrUtil.hide(user.getPhone(), 3, 7));

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userInfo(userVO)
                .build();
    }

    private void handleFailedLogin(User user) {
        int failedAttempts = user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts();
        failedAttempts++;
        user.setFailedLoginAttempts(failedAttempts);
        
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockoutTime(LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
            user.setFailedLoginAttempts(0); // Reset attempts after lock or keep? keeping 0 makes sense if we lock.
        }
        userService.updateById(user);
    }

    private void handleSuccessfulLogin(User user) {
        if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setLockoutTime(null);
            userService.updateById(user);
        }
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        
        // 1. 验证 Refresh Token
        if (!jwtUtil.validateRefreshToken(token)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "无效的刷新令牌");
        }

        // 2. 获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 3. 检查锁定状态
        if (user.getLockoutTime() != null && user.getLockoutTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "账号已锁定，请稍后再试");
        }

        // 4. 生成新 Token
        String accessToken = jwtUtil.generateToken(user.getId(), user.getPhone());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 5. 构建响应
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        userVO.setPhone(StrUtil.hide(user.getPhone(), 3, 7));

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userInfo(userVO)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterRequest request) {
        log.info("Register attempt for phone: {}", request.getPhone());

        // 1. 验证短信验证码
        if (!smsService.verify(request.getPhone(), request.getSmsCode(), SmsType.REGISTER)) {
            throw new BusinessException(ErrorCode.SMS_CODE_ERROR);
        }

        // 2. 检查手机号是否已存在
        if (userService.getByPhone(request.getPhone()) != null) {
            throw new BusinessException(ErrorCode.USER_PHONE_EXIST);
        }

        // 3. 创建用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname("User_" + StrUtil.subSuf(request.getPhone(), 7));
        user.setStatus(1);
        user.setRegisterType("phone");
        user.setFailedLoginAttempts(0);
        
        userService.save(user);
        log.info("User registered successfully: {}", user.getId());
        
        return user.getId();
    }

    @Override
    public String sendResetToken(ResetPasswordTokenRequest request) {
        // 1. 验证短信验证码
        if (!smsService.verify(request.getPhone(), request.getSmsCode(), SmsType.RESET_PASSWORD)) {
            throw new BusinessException(ErrorCode.SMS_CODE_ERROR);
        }
        
        // 2. 检查用户是否存在
        User user = userService.getByPhone(request.getPhone());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 3. 生成一次性 Token (简化使用 JWT，有效期 5 分钟)
        // 实际场景可能需要 Redis 存储来保证一次性
        return jwtUtil.generateToken(user.getId(), user.getPhone());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordRequest request) {
        // 1. 验证 Token
        if (!jwtUtil.validateToken(request.getToken())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(request.getToken());
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 2. 验证新密码不能与旧密码相同 (可选)
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "新密码不能与旧密码相同");
        }

        // 3. 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.updateById(user);
        log.info("Password reset successfully for user: {}", userId);
    }
}
