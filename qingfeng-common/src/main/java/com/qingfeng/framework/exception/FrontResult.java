package com.qingfeng.framework.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrontResult {
    /**
     * 结果状态码
     */
    private String code;
    /**
     * 响应结果描述
     */
    private String message;
    /**
     * 返回数据
     */
    private Object data;

    /**
     * 静态方法，返回前端实体结果
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     * @return 前端实体结果
     */
    public static FrontResult build(String code, String message, Object data) {
        return new FrontResult(code, message, data);
    }

    /**
     * 返回成功的结果实体
     *
     * @param message 消息
     * @param data    数据
     * @return 实体
     */
    public static FrontResult getSuccessResult(String message, Object data) {
        FrontResult result = new FrontResult();
        result.code = ResultCodeEnum.SUCCESS.getCode();
        result.message = message;
        result.data = data;
        return result;
    }

    /**
     * 返回无需data的成功结果实体
     *
     * @param message 消息内容
     * @return 返回结果
     */
    public static FrontResult getSuccessResultOnlyMessage(String message) {
        FrontResult result = new FrontResult();
        result.code = ResultCodeEnum.SUCCESS.getCode();
        result.message = message;
        result.data = null;
        return result;
    }

    /**
     * 获取一个异常结果
     *
     * @param code 错误码
     * @param message 自定义异常信息
     * @return FrontResult
     */
    public static FrontResult getExceptionResult(String code, String message) {
        FrontResult result = new FrontResult();
        result.code = code.isEmpty() ? ResultCodeEnum.FAIL.getCode() : code;
        result.message = message.isEmpty() ? ResultCodeEnum.FAIL.getMsg() : message;
        return result;
    }
}

