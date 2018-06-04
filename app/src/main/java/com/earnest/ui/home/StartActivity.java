package com.earnest.ui.home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.earnest.R;
import com.earnest.manager.BmobManager;
import com.earnest.model.entities.MyUser;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_Start_Login;
    private Button btn_Start_Register;
    private Intent startIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initView();
        Bmob.initialize(this, BmobManager.BmobAppID);

        // 获取登录用户缓存
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null) {
            // 允许用户使用应用
            startIntent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(startIntent);
        }
        else {
            // 缓存用户对象为空时， 可打开用户登录、注册界面
            Toast.makeText(this, "debug", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Start_Login:
                // TODO
                startIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(startIntent);
                break;
            case R.id.btn_Start_Register:
                // TODO
                startIntent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(startIntent);
                break;
            default:
                // TODO
                break;
        }
    }

    private void initView() {
        btn_Start_Login = (Button) findViewById(R.id.btn_Start_Login);
        btn_Start_Register = (Button) findViewById(R.id.btn_Start_Register);

        btn_Start_Login.setOnClickListener(this);
        btn_Start_Register.setOnClickListener(this);
    }

//    private void
}
