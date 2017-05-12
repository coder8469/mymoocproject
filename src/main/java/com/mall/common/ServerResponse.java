package com.mall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 *  服务响应对象，
 * Created by Administrator on 2017-4-30.
 */
//空的字段在序列化后不会返回给客户端
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    private int status;     //返回给前端的状态码
    private String msg;     //返回给前端的提示信息
    private T data;         //返回给前端的数据

    private ServerResponse(int status){
        this.status = status;
    }
    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    private ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }
    //使之不在json序列化范围内
    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }
    public  int getStatus(){
        return this.status;
    }
    public T getData(){
       return this.data;
    }
    public String getMsg(){
        return this.msg;
    }
    //创建一个返回成功的方法，不带参数
    public static <T> ServerResponse<T> createBySccess(){
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
    }
    //创建一个需要传入msg参数的方法
    public static <T> ServerResponse<T> createBySccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    //创建一个需要传入data参数的方法
    public static <T> ServerResponse<T> createBySccessMessage(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    //创建一个需要传入msg 和 data 参数的方法
    public static <T> ServerResponse<T> createBySccessMessage(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    public static <T> ServerResponse<T> createByErrorMessage(String errorMsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMsg);
    }
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMsg){
        return new ServerResponse<T>(errorCode,errorMsg);
    }
}
