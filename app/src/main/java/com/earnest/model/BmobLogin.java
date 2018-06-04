package com.earnest.model;

import android.widget.EditText;
import android.widget.Toast;

import com.earnest.ui.home.LoginActivity;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by ZhuShuLi on 2018/6/3.
 */

public class BmobLogin {
    private EditText et_Login_Phonenumber;
    private EditText et_Login_Password;

    public BmobLogin(EditText etUserTel, EditText etPassword) {
        this.et_Login_Phonenumber = etUserTel;
        this.et_Login_Password = etPassword;
    }

    public void login() {
        String userTel = et_Login_Phonenumber.getText().toString();
        String password = et_Login_Password.getText().toString();

        if (!checkInput(userTel, password))
            return ;

        Toast.makeText(LoginActivity.instance, "正在登录...", Toast.LENGTH_SHORT).show();

        BmobUser user = new BmobUser();
        user.setUsername(userTel);
        user.setPassword(password);
        user.login(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if(e == null) {
                    Toast.makeText(LoginActivity.instance, "登录成功", Toast.LENGTH_SHORT).show();
                    // 如果成功后，延时3秒后返回上一个页面
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            LoginActivity.instance.finish();
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 3000);
                }
                else {
                    Toast.makeText(LoginActivity.instance, "密码或帐号错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkInput (String userTel, String password) {
        if (userTel.length() == 0) {
            Toast.makeText(LoginActivity.instance, "请输入手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (password.length() == 0) {
            Toast.makeText(LoginActivity.instance, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (userTel.length() != 11) {
            Toast.makeText(LoginActivity.instance, "手机号无效", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
