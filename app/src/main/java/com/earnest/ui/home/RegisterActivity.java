package com.earnest.ui.home;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.earnest.R;
import com.earnest.manager.BmobManager;
import com.earnest.model.BmobRegister;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_Register_Phonenumber;
    private EditText et_VerifyCode;
    private EditText et_Password;
    private Button btn_SendCode;
    private Button btn_Register;

    private String phoneNumber;
    private String verifyCode;
    private BmobRegister bmobRegister;

    public static RegisterActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        initView();
        BmobSMS.initialize(RegisterActivity.this, BmobManager.BmobAppID);

        instance = this;
        bmobRegister = new BmobRegister(et_Register_Phonenumber, et_VerifyCode, et_Password, btn_SendCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_SendCode:
                // TODO
                bmobRegister.getVerifyCode();
                // getVerifyCode();
                break;
            case R.id.btn_Register:
                // TODO
                // checkVerifyCode();
                bmobRegister.register();
                break;
            default:
                // TODO
                break;
        }
    }

    private void initView() {
        et_Register_Phonenumber = (EditText) findViewById(R.id.et_Register_Phonenumber);
        et_VerifyCode = (EditText) findViewById(R.id.et_VerifyCode);
        et_Password = (EditText) findViewById(R.id.et_Password);

        btn_SendCode = (Button) findViewById(R.id.btn_SendCode);
        btn_Register = (Button) findViewById(R.id.btn_Register);

        btn_SendCode.setOnClickListener(this);
        btn_Register.setOnClickListener(this);
    }

    private void getVerifyCode() {
        phoneNumber = et_Register_Phonenumber.getText().toString();
        if (phoneNumber.length() != 11) {
            Toast.makeText(RegisterActivity.this, "输入的手机号无效", Toast.LENGTH_SHORT).show();
            return ;
        }

        BmobSMS.requestSMSCode(RegisterActivity.this, phoneNumber, "EarNest短信", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {        // 验证码发送成功
                    btn_SendCode.setClickable(false);
                    new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            btn_SendCode.setText(millisUntilFinished / 1000 + "秒");
                        }
                        @Override
                        public void onFinish() {
                            btn_SendCode.setClickable(true);
                            btn_SendCode.setText("重新发送");
                        }
                    }.start();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkVerifyCode() {
        verifyCode = et_VerifyCode.getText().toString();
        if (verifyCode.length() != 6) {
            Toast.makeText(RegisterActivity.this, "验证码格式错误", Toast.LENGTH_SHORT).show();
            return false;
        }

        BmobSMS.verifySmsCode(RegisterActivity.this, phoneNumber, verifyCode, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException e) {
                if(e == null) {     // 短信验证码已通过
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(RegisterActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                    et_VerifyCode.setText("");
                }
            }
        });

        return true;
    }
}
