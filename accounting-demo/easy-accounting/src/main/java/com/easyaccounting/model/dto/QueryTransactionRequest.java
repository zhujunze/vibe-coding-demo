package com.easyaccounting.model.dto;

import com.easyaccounting.common.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Schema(description = "账单列表查询请求")
public class QueryTransactionRequest {

    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "查询年份", example = "2023")
    private Integer year;

    @Schema(description = "查询月份", example = "10")
    private Integer month;

    @Schema(description = "交易类型", example = "EXPENSE")
    private TransactionType type;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "开始日期", example = "2023-10-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2023-10-31")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
