package com.qingfeng.utils;

import java.util.HashMap;

/**
 * @ProjectName MyResponse
 * @author Administrator
 * @version 1.0.0
 * @Description 公共返回参数
 * @createTime 2022/1/12 9:01
 */
public class MyResponse extends HashMap<String, Object> {

    private static final long serialVersionUID = -8713837118340960775L;

    public MyResponse message(String message) {
        this.put("message", message);
        return this;
    }

    public MyResponse data(Object data) {
        this.put("data", data);
        return this;
    }

    @Override
    public MyResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public String getMessage() {
        return String.valueOf(get("message"));
    }

    public Object getData() {
        return get("data");
    }
}
