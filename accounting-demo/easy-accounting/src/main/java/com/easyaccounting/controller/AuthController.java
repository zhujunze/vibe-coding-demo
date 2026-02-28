package com.easyaccounting.controller;

import com.easyaccounting.common.result.Result;
import com.easyaccounting.model.dto.*;
import com.easyaccounting.model.vo.LoginResponse;
import com.easyaccounting.service.IAuthService;
import com.easyaccounting.service.ISmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户登录、注册相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final ISmsService smsService;

    @Operation(summary = "用户登录", description = "支持手机号/邮箱 + 密码登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @Operation(summary = "用户注册", description = "手机号 + 密码 + 短信验证码注册")
    @PostMapping("/register")
    public Result<Long> register(@RequestBody @Valid RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @Operation(summary = "发送短信验证码", description = "向指定手机号发送验证码")
    @PostMapping("/send-sms")
    public Result<Void> sendSms(@RequestBody @Valid SendSmsRequest request) {
        smsService.send(request.getPhone(), "REGISTER"); // 默认模板
        return Result.success(null);
    }

    @Operation(summary = "获取重置密码Token", description = "验证手机号和验证码，获取重置密码的一次性Token")
    @PostMapping("/reset-password-token")
    public Result<String> getResetToken(@RequestBody @Valid ResetPasswordTokenRequest request) {
        return Result.success(authService.sendResetToken(request));
    }

    @Operation(summary = "重置密码", description = "使用Token重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return Result.success(null);
    }
}
