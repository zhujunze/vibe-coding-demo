package com.easyaccounting.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 重置密码请求参数
 */
@Data
@Schema(description = "重置密码请求参数")
public class ResetPasswordRequest {

    @Schema(description = "重置Token", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Token不能为空")
    private String token;

    @Schema(description = "新密码", example = "NewPassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
