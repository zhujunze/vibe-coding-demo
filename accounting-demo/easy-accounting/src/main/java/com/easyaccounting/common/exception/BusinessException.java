package com.easyaccounting.common.exception;

import com.easyaccounting.common.result.Result;
import lombok.Getter;

/**
 * 业务异常
 *
 * <p>用于业务逻辑处理中主动抛出的异常，包含错误码。</p>
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
