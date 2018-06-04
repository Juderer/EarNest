package com.earnest.ui.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.earnest.R;
import com.earnest.manager.BmobManager;
import com.earnest.model.BmobLogin;

import cn.bmob.v3.Bmob;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_Login_Phonenumber;
    private EditText et_Login_Password;
    private Button btn_Login;

    private BmobLogin bmobLogin;

    public static LoginActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        initView();
        Bmob.initialize(LoginActivity.this, BmobManager.BmobAppID);

        bmobLogin = new BmobLogin(et_Login_Phonenumber, et_Login_Password);
        instance = this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Login:
                // TODO
                bmobLogin.login();
                break;
            default:
                // TODO
                break;
        }
    }

    private void initView() {
        et_Login_Phonenumber = (EditText) findViewById(R.id.et_Login_Phonenumber);
        et_Login_Password = (EditText) findViewById(R.id.et_Login_Password);

        btn_Login = (Button) findViewById(R.id.btn_Login);
        btn_Login.setOnClickListener(this);
    }
}
