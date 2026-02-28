package com.easyaccounting.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一 API 响应包装类
 *
 * <p>规范所有控制层接口的返回格式。</p>
 *
 * @param <T> 数据载荷类型
 */
@Data
@Schema(description = "统一响应包装类")
public class Result<T> implements Serializable {

    @Schema(description = "状态码", example = "200")
    private Integer code;

    @Schema(description = "状态信息", example = "success")
    private String message;

    @Schema(description = "响应数据载荷")
    private T data;

    @Schema(description = "追踪 ID (用于故障排查)", example = "c0a80101-...")
    private String traceId;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
