package com.easyaccounting.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 交易类型枚举
 */
@Getter
public enum TransactionType {
    EXPENSE("expense", "支出"),
    INCOME("income", "收入");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    TransactionType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
