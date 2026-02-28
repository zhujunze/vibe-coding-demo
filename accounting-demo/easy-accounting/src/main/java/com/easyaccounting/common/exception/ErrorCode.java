package com.easyaccounting.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * <p>规范：三段式错误码 (A-BB-CC)
 * A: 系统标识 (1: 记账系统)
 * BB: 模块标识 (00: 通用, 01: 用户, 02: 账单, 03: 分类, 04: 预算, 05: 图表)
 * CC: 错误序号
 * </p>
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 通用错误 (10000 - 10099)
    SUCCESS(200, "操作成功"),
    SYSTEM_ERROR(500, "系统内部错误"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    REQUEST_LIMIT(429, "请求过于频繁"),

    // 用户模块 (10100 - 10199)
    USER_NOT_EXIST(10101, "用户不存在"),
    USER_PASSWORD_ERROR(10102, "密码错误"),
    USER_PHONE_EXIST(10103, "手机号已存在"),
    SMS_CODE_ERROR(10104, "验证码错误或已过期"),

    // 账单模块 (10200 - 10299)
    TRANSACTION_NOT_EXIST(10201, "账单记录不存在"),
    TRANSACTION_ACCESS_DENIED(10202, "无权操作该账单"),

    // 分类模块 (10300 - 10399)
    CATEGORY_NOT_EXIST(10301, "分类不存在"),
    CATEGORY_NAME_EXIST(10302, "分类名称已存在"),
    SYSTEM_CATEGORY_DELETE_ERROR(10303, "系统预置分类不可删除"),

    // 预算模块 (10400 - 10499)
    BUDGET_ALREADY_EXIST(10401, "该月预算已设置"),
    
    // 图表模块 (10500 - 10599)
    INVALID_PERIOD(10501, "无效的时间周期");

    private final Integer code;
    private final String message;
}
