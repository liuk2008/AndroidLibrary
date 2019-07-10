package com.android.network.error;

import java.io.Serializable;

/**
 * 网络异常数据模型
 * Created by Administrator on 2018/4/10.
 */

public class ErrorData implements Serializable {

    private static final long serialVersionUID = 835238407293265994L;
    private int code;
    private String msg;
    private String data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ErrorData{");
        sb.append("code=").append(code);
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", data='").append(data).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
