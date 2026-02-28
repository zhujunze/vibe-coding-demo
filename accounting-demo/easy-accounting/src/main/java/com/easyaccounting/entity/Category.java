package com.easyaccounting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyaccounting.common.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账单分类实体类
 *
 * <p>映射数据库中的 categories 表。</p>
 */
@Data
@TableName("categories")
@Schema(description = "账单分类实体")
public class Category {

    @TableId(type = IdType.AUTO)
    @Schema(description = "分类 ID", example = "1")
    private Long id;

    @Schema(description = "所属用户 ID (NULL表示系统预置)", example = "1")
    private Long userId;

    @Schema(description = "分类名称", example = "餐饮")
    private String name;

    @Schema(description = "类型 (expense-支出，income-收入)", example = "expense")
    private CategoryType type;

    @Schema(description = "图标标识", example = "food")
    private String icon;

    @Schema(description = "颜色值(十六进制)", example = "#FF6B6B")
    private String color;

    @Schema(description = "排序权重", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否系统预置(1-是，0-否)", example = "1")
    private Integer isSystem;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
