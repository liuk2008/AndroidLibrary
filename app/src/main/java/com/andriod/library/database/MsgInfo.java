package com.andriod.library.database;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class MsgInfo implements Serializable {

    private static final long serialVersionUID = -1;
    @SerializedName("msg_title")
    public String title;
    @SerializedName("msg_content")
    public String content;
    @SerializedName("msg_status")
    public int status;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MsgInfo{");
        sb.append("title='").append(title).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
