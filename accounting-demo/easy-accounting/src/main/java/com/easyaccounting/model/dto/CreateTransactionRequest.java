package com.easyaccounting.model.dto;

import com.easyaccounting.common.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建交易请求参数
 */
@Data
@Schema(description = "创建交易请求参数")
public class CreateTransactionRequest {

    @Schema(description = "分类ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "金额", example = "100.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "类型(expense/income)", example = "expense", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "类型不能为空")
    private TransactionType type;

    @Schema(description = "日期", example = "2023-10-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @Schema(description = "备注", example = "午餐")
    private String note;
}
