package com.andriod.library.network.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = -1;
    @SerializedName("phone")
    public String phone;
    @SerializedName("userId")
    public String userId;
    @SerializedName("keepOnline")
    public boolean keepOnline;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserInfo{");
        sb.append("phone='").append(phone).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", keepOnline=").append(keepOnline);
        sb.append('}');
        return sb.toString();
    }
}
