package com.easyaccounting.service;

import com.easyaccounting.model.dto.LoginRequest;
import com.easyaccounting.model.dto.RefreshTokenRequest;
import com.easyaccounting.model.dto.RegisterRequest;
import com.easyaccounting.model.dto.ResetPasswordRequest;
import com.easyaccounting.model.dto.ResetPasswordTokenRequest;
import com.easyaccounting.model.vo.LoginResponse;

public interface IAuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 刷新令牌
     *
     * @param request 刷新请求
     * @return 登录响应
     */
    LoginResponse refreshToken(RefreshTokenRequest request);

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户ID
     */
    Long register(RegisterRequest request);

    /**
     * 发送重置密码Token
     *
     * @param request 请求
     * @return 临时Token
     */
    String sendResetToken(ResetPasswordTokenRequest request);

    /**
     * 重置密码
     *
     * @param request 重置密码请求
     */
    void resetPassword(ResetPasswordRequest request);
}
