package com.easyaccounting.model.dto;

import com.easyaccounting.common.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "更新账单请求")
public class UpdateTransactionRequest {

    @Schema(description = "账单ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "账单ID不能为空")
    private Long id;

    @Schema(description = "金额", example = "100.00")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "交易类型", example = "EXPENSE")
    private TransactionType type;

    @Schema(description = "日期", example = "2023-10-01")
    private LocalDate date;

    @Schema(description = "备注", example = "餐饮")
    private String note;
}
