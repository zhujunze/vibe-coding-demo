package com.easyaccounting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账单记录实体类
 *
 * <p>映射数据库中的 transactions 表。</p>
 */
@Data
@TableName("transactions")
@Schema(description = "账单记录实体")
public class Transaction {

    @TableId(type = IdType.AUTO)
    @Schema(description = "记录 ID", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "分类 ID", example = "10")
    private Long categoryId;

    @Schema(description = "金额", example = "128.50")
    private BigDecimal amount;

    @Schema(description = "类型 (expense-支出，income-收入)", example = "expense")
    private String type;

    @Schema(description = "记账日期", example = "2023-10-01")
    private LocalDate date;

    @Schema(description = "备注", example = "周末聚餐")
    private String note;

    @Schema(description = "同步状态(1-已同步，0-待同步)", example = "1")
    private Integer syncStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
