package com.earnest.manager;

import android.content.Intent;
import android.widget.Toast;

import com.earnest.model.entities.MyUser;
import com.earnest.ui.home.MainActivity;
import com.earnest.ui.home.StartActivity;

import cn.bmob.v3.BmobUser;

public class BmobManager {
    public static String BmobAppID = "181be3ef7b4de58c4cd36cc2a100159e";       // Bmob Application ID

    public static void checkUserInfo() {
        // 获取登录用户缓存
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null) {
            // 允许用户使用应用
            Toast.makeText(StartActivity.instance, "已经登录过", Toast.LENGTH_SHORT).show();
            Intent startIntent = new Intent(StartActivity.instance, MainActivity.class);
            StartActivity.instance.startActivity(startIntent);
        }
        else {
            // 缓存用户对象为空时， 可打开用户登录、注册界面
//            Toast.makeText(StartActivity.instance, "未登录", Toast.LENGTH_SHORT).show();
        }
    }
}
