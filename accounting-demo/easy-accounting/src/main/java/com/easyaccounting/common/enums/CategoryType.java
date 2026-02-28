package com.easyaccounting.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 分类类型枚举
 */
@Getter
public enum CategoryType {
    EXPENSE("expense", "支出"),
    INCOME("income", "收入");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    CategoryType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CategoryType of(String code) {
        for (CategoryType type : values()) {
            if (type.code.equalsIgnoreCase(code) || type.name().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
