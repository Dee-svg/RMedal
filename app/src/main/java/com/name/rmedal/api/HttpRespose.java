package com.name.rmedal.api;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * 封装服务器返回数据
 * 类中的参数是可以修改的
 */
public class HttpRespose<T> {

    private int code;
    private String message;
    private T result;

    public boolean success() {
        return code == 200;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
