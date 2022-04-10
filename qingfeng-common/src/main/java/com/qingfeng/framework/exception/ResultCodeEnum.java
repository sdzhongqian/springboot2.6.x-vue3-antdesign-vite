package com.qingfeng.framework.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResultCodeEnum {
    // 操作成功
    SUCCESS("200"),
    // 操作失败
    FAIL("500", "代码内部异常");

    /**
     * 状态码
     */
    private String code;

    public String getCode() {
        return code;
    }

    ResultCodeEnum(String code) {
        this.code = code;
    }

    private String msg;

    public String getMsg() {
        return msg;
    }

}

