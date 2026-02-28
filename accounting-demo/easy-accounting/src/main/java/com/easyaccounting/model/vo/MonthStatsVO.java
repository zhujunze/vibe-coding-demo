package com.easyaccounting.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "月度收支统计视图")
public class MonthStatsVO {

    @Schema(description = "年份", example = "2023")
    private Integer year;

    @Schema(description = "月份", example = "10")
    private Integer month;

    @Schema(description = "总收入", example = "5000.00")
    private BigDecimal totalIncome;

    @Schema(description = "总支出", example = "3000.00")
    private BigDecimal totalExpense;

    @Schema(description = "结余", example = "2000.00")
    private BigDecimal balance;
}
