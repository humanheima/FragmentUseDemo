package com.hm.fragmentusedemo.utils;

import com.google.gson.annotations.SerializedName;

public class MsgResult<T> {

    public int code;
    public String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @SerializedName("data")
    public T result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

}
