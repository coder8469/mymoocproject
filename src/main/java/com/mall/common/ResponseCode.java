package com.mall.common;

/**
 * 用来返回给前端对应请求的提示信息
 * Created by Administrator on 2017-4-30.
 */
public enum  ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),    
    ILLEAGE_ARGUMENT(2,"ILLEAGE_ARGUMENT");

    private  final  int code;
    private final String desc;

    ResponseCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
