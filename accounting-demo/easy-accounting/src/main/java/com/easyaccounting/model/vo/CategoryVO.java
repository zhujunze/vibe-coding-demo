package com.easyaccounting.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分类信息视图")
public class CategoryVO {

    @Schema(description = "分类ID", example = "1")
    private Long id;

    @Schema(description = "分类名称", example = "餐饮")
    private String name;

    @Schema(description = "分类类型(expense/income)", example = "expense")
    private String type;

    @Schema(description = "图标标识", example = "food")
    private String icon;

    @Schema(description = "颜色值", example = "#FF0000")
    private String color;

    @Schema(description = "是否系统预置", example = "true")
    private Boolean isSystem;
}
