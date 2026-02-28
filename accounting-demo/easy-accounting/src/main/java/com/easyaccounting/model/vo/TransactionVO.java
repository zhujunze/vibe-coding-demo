package com.easyaccounting.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "账单列表项视图")
public class TransactionVO {

    @Schema(description = "账单ID", example = "1")
    private Long id;

    @Schema(description = "分类名称", example = "餐饮")
    private String categoryName;

    @Schema(description = "分类图标", example = "food")
    private String categoryIcon;
    
    @Schema(description = "分类颜色", example = "#FF0000")
    private String categoryColor;

    @Schema(description = "交易类型(支出/收入)", example = "支出")
    private String type;

    @Schema(description = "金额", example = "100.00")
    private BigDecimal amount;

    @Schema(description = "日期", example = "2023-10-01")
    private LocalDate date;
    
    @Schema(description = "备注", example = "午餐")
    private String note;
}
