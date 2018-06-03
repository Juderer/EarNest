package com.earnest.model.entities;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/5/29.
 */

public class MyUser extends BmobUser {//用户类，添加用户基本属性及对应方法
    private Boolean gender;     // true，性别女；false，性别男
    private String nickname;    // 用户昵称
    private Integer age;        // 用户年龄

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return getUsername()+"\n"+getObjectId()+"\n"+gender+"\n"+age+"\n"+nickname+"\n";
    }
}