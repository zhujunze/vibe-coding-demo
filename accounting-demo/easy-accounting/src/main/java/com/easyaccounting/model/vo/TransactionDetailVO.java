package com.easyaccounting.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "账单详情视图")
public class TransactionDetailVO extends TransactionVO {

    @Schema(description = "分类ID", example = "10")
    private Long categoryId;

    @Schema(description = "创建时间", example = "2023-10-01 12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2023-10-01 12:00:00")
    private LocalDateTime updatedAt;
}
