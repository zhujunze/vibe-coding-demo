package com.easyaccounting.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 设置预算请求参数
 */
@Data
@Schema(description = "设置预算请求参数")
public class SetBudgetRequest {

    @Schema(description = "分类ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "月份(YYYY-MM)", example = "2023-10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "月份不能为空")
    private String month;

    @Schema(description = "预算金额", example = "2000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预算金额不能为空")
    private BigDecimal amount;

    @Schema(description = "预警阈值", example = "0.80")
    private BigDecimal alertThreshold;
}
