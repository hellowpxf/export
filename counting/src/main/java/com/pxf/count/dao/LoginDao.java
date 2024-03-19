package com.pxf.count.dao;

import java.util.Date;

/**
 * @description:LoginDao
 * @author:pxf
 * @data:2024/02/18
 **/
public class LoginDao {
    private User user;
    private Date lastLoginTime;
    private int type;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LoginService.xml{" +
                "user=" + user +
                ", lastLoginTime=" + lastLoginTime +
                ", type=" + type +
                '}';
    }
}
