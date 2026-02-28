package com.easyaccounting.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户信息视图")
public class UserVO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "手机号(脱敏)", example = "138****0000")
    private String phone;

    @Schema(description = "昵称", example = "用户123")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.png")
    private String avatarUrl;

    @Schema(description = "注册时间", example = "2023-10-01 10:00:00")
    private LocalDateTime createdAt;
}
