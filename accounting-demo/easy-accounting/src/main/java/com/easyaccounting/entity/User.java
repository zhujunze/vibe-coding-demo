package com.easyaccounting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * <p>映射数据库中的 users 表。</p>
 */
@Data
@TableName("users")
@Schema(description = "用户实体")
public class User {

    @TableId(type = IdType.AUTO)
    @Schema(description = "用户 ID", example = "1")
    private Long id;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "密码 (已加密)", example = "******")
    private String password;

    @Schema(description = "昵称", example = "John Doe")
    private String nickname;

    @Schema(description = "头像 URL", example = "https://example.com/avatar.png")
    private String avatarUrl;

    @Schema(description = "状态 (1: 正常, 0: 禁用)", example = "1")
    private Integer status;

    @Schema(description = "注册方式 (phone, guest)", example = "phone")
    private String registerType;

    @Schema(description = "登录失败次数")
    private Integer failedLoginAttempts;

    @Schema(description = "锁定截止时间")
    private LocalDateTime lockoutTime;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
