package com.easyaccounting.model.dto;

import com.easyaccounting.common.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建分类请求参数
 */
@Data
@Schema(description = "创建分类请求参数")
public class CreateCategoryRequest {

    @Schema(description = "分类名称", example = "游戏", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @Schema(description = "类型(expense/income)", example = "expense", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "类型不能为空")
    private CategoryType type;

    @Schema(description = "图标", example = "game", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "图标不能为空")
    private String icon;

    @Schema(description = "颜色", example = "#000000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "颜色不能为空")
    private String color;
}
