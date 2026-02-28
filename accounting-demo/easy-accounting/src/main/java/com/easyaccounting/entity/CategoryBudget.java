package com.easyaccounting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 分类预算实体类
 *
 * <p>映射数据库中的 category_budgets 表。</p>
 */
@Data
@TableName("category_budgets")
@Schema(description = "分类预算实体")
public class CategoryBudget {

    @TableId(type = IdType.AUTO)
    @Schema(description = "预算 ID", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "分类 ID", example = "10")
    private Long categoryId;

    @Schema(description = "预算月份(YYYY-MM)", example = "2023-10")
    private String month;

    @Schema(description = "预算金额", example = "2000.00")
    private BigDecimal budgetAmount;

    @Schema(description = "预警阈值(默认0.80)", example = "0.80")
    private BigDecimal alertThreshold;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
