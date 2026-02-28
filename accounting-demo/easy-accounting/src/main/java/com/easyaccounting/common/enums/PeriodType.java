package com.easyaccounting.common.enums;

import lombok.Getter;

/**
 * 统计周期枚举
 */
@Getter
public enum PeriodType {
    YEAR("按年"),
    MONTH("按月"),
    WEEK("按周");

    private final String desc;

    PeriodType(String desc) {
        this.desc = desc;
    }
}
