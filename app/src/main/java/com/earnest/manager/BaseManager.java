package com.earnest.manager;

import com.earnest.model.User;

/**
 * Created by Administrator on 2018/5/29.
 */

public class BaseManager {
    protected User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public  boolean isLogined() {
        return user != null;
    }
}
