package com.earnest.ui.home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.earnest.R;
import com.earnest.manager.BmobManager;
import com.earnest.model.entities.MyUser;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_Start_Login;
    private TextView tv_Start_Register;
    private Intent startIntent;

    public static StartActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initView();
        Bmob.initialize(this, BmobManager.BmobAppID);
        instance = this;

        // 检查用户是否已经登录过
        BmobManager.checkUserInfo();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 检查用户是否已经登录过
        BmobManager.checkUserInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Start_Login:
                // TODO
                startIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(startIntent);
                break;
            case R.id.tv_Start_Register:
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
        tv_Start_Login = (TextView) findViewById(R.id.tv_Start_Login);
        tv_Start_Register = (TextView) findViewById(R.id.tv_Start_Register);

        tv_Start_Login.setOnClickListener(this);
        tv_Start_Register.setOnClickListener(this);
    }
}