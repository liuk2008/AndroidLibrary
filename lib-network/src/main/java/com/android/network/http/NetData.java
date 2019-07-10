package com.android.network.http;

import java.io.Serializable;

/**
 * 原生网络框架数据模型
 * 1、解析业务层数据
 * 2、解析网络异常数据
 * Created by Administrator on 2018/4/10.
 */
public class NetData implements Serializable {

    private static final long serialVersionUID = 835238407293265993L;
    private int code;
    private String msg;
    private String data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NetData{");
        sb.append("code=").append(code);
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", data='").append(data).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
