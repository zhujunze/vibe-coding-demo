package com.easyaccounting.model.dto;

import com.easyaccounting.model.enums.SmsType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "发送短信验证码请求")
public class SendSmsRequest {

    @Schema(description = "手机号", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "验证码类型", example = "REGISTER", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "验证码类型不能为空")
    private SmsType type;
}
