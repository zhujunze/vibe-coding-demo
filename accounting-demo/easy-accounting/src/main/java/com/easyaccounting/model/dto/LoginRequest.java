package com.easyaccounting.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求参数
 */
@Data
@Schema(description = "登录请求参数")
public class LoginRequest {

    @Schema(description = "手机号/邮箱", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "账号不能为空")
    private String account;

    @Schema(description = "密码", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "设备信息", example = "iPhone 14 Pro")
    private String deviceInfo;

    @Schema(description = "图形验证码", example = "AB12")
    private String captcha;
}
