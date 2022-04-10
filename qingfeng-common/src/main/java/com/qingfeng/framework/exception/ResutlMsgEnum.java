package com.qingfeng.framework.exception;

public enum ResutlMsgEnum {

    //操作成功
    SUCCESS("操作成功！"),
    //操作失败
    FAIL("操作失败！"),

    //空指针异常
    NULLPOINTERFAIL("空指针异常！"),
    //索引越界异常
    INDEXOUTOFBOUNDSFAIL("索引越界异常！"),
    //处理类未找到异常
    CLASSNOTFOUNDFIL("处理类未找到异常！"),
    //处理SQL异常
    SQLFAIL("处理SQL异常！"),
    //处理SQL异常
    IOFAIL("处理IO异常！"),
    //其他异常
    OTHERFAIL("系统内部异常！");


    private String msg;

    ResutlMsgEnum(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}