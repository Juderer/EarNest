package com.earnest.manager;

import com.earnest.model.User;

/**
 * Created by Administrator on 2018/5/29.
 */

public class UserManager extends BaseManager{//实现单实例模式，进行用户User类的管理，部分方法继承BaseManager
    public UserManager() {}
    private static UserManager instance;

    public static  UserManager getInstance() {
        if(instance == null) {
            instance = new UserManager();
        }
        return  instance;
    }
}
