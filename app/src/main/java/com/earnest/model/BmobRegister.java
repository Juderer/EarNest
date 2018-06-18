package com.earnest.model;

import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.earnest.model.entities.MyUser;
import com.earnest.ui.home.LoginActivity;
import com.earnest.ui.home.RegisterActivity;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
//import cn.bmob.v3.BmobUser;
//import cn.bmob.v3.exception.BmobException;
//import cn.bmob.v3.listener.SaveListener;

/**
 * Created by ZhuShuLi on 2018/6/2.
 */

public class BmobRegister {
    private EditText et_Register_Phonenumber;
    private EditText et_VerifyCode;
    private EditText et_Password;
    private Button btn_SendCode;

    private String phoneNumber;
    private String verifyCode;
    private String password;

    private boolean isCorrectVerifyCode = false;

    public BmobRegister(EditText etRegisterTel, EditText etVerifyCode, EditText etPasswd, Button btnSendCode) {
        this.et_Register_Phonenumber = etRegisterTel;
        this.et_VerifyCode = etVerifyCode;
        this.et_Password = etPasswd;
        this.btn_SendCode = btnSendCode;
    }

    public void getVerifyCode() {
        phoneNumber = et_Register_Phonenumber.getText().toString();
        if (phoneNumber.length() != 11) {
            Toast.makeText(RegisterActivity.instance, "输入的手机号无效", Toast.LENGTH_SHORT).show();
            return ;
        }

        BmobSMS.requestSMSCode(RegisterActivity.instance, phoneNumber, "EarNest短信", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, cn.bmob.sms.exception.BmobException e) {
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
                    Toast.makeText(RegisterActivity.instance, "获取验证码失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void register() {
        phoneNumber = et_Register_Phonenumber.getText().toString();
        password = et_Password.getText().toString();

        if (!checkInput(phoneNumber, password))
            return ;
        if (!checkVerifyCode())
            return ;

        BmobSMS.verifySmsCode(RegisterActivity.instance, phoneNumber, verifyCode, new VerifySMSCodeListener() {
            @Override
            public void done(cn.bmob.sms.exception.BmobException e) {
                if(e == null) {     // 短信验证码已通过
//                    Intent intent = new Intent(RegisterActivity.instance, LoginActivity.class);
//                    RegisterActivity.instance.startActivity(intent);
                    isCorrectVerifyCode = true;
                }
                else {
                    Toast.makeText(RegisterActivity.instance, "验证失败", Toast.LENGTH_SHORT).show();
                    et_VerifyCode.setText("");
                }
            }
        });
        if (!isCorrectVerifyCode)
            return ;

//        MyUser user = new MyUser();
//        user.setUsername(phoneNumber);
//        user.setPassword(password);
//        user.setMobilePhoneNumber(phoneNumber);
//        user.signUp(new SaveListener<BmobUser>() {
//            @Override
//            public void done(BmobUser bmobUser, BmobException e) {
//                if (e == null) {
//                    Toast.makeText(RegisterActivity.instance, "注册成功", Toast.LENGTH_SHORT).show();
//                    // 如果成功后，延时1.5秒后返回上一个页面
//                    TimerTask task = new TimerTask() {
//                        @Override
//                        public void run() {
//                            RegisterActivity.instance.finish();
//                        }
//                    };
//                    Timer timer = new Timer();
//                    timer.schedule(task, 1500);
//                }
//                else {
//                    Toast.makeText(RegisterActivity.instance, "注册失败", Toast.LENGTH_SHORT).show();
//                    et_Register_Phonenumber.setText("");
//                    et_Password.setText("");
//                }
//            }
//        });
    }

    private boolean checkInput(String userTel, String password) {
        if (userTel.length() == 0) {
            Toast.makeText(RegisterActivity.instance, "请输入手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (password.length() == 0) {
            Toast.makeText(RegisterActivity.instance, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (userTel.length() != 11) {
            Toast.makeText(RegisterActivity.instance, "手机号无效", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkVerifyCode() {
        verifyCode = et_VerifyCode.getText().toString();
        if (verifyCode.length() != 6) {
            Toast.makeText(RegisterActivity.instance, "验证码格式错误", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
