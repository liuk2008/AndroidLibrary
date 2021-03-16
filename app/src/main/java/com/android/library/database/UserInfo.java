package com.android.library.database;


import com.android.database.annotation.Column;
import com.android.database.annotation.Table;

import java.io.Serializable;

@Table(name = "userinfo")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -1;
    @Column(name = "user_name")
    public String name = "";
    @Column(name = "user_age")
    public int age;
    @Column(name = "user_phone")
    public long phone;
    @Column(name = "user_account")
    public float account;
    @Column(name = "user_remain")
    public double remain;
    @Column(name = "isDelete")
    public boolean isDelete;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserInfo{");
        sb.append("name='").append(name).append('\'');
        sb.append(", age=").append(age);
        sb.append(", phone=").append(phone);
        sb.append(", account=").append(account);
        sb.append(", remain=").append(remain);
        sb.append(", isDelete=").append(isDelete);
        sb.append('}');
        return sb.toString();
    }
}
