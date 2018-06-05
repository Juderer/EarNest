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
}