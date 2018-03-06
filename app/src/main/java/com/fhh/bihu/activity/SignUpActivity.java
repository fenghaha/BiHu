package com.fhh.bihu.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.fhh.bihu.R;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.JsonParse;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.MyTextUtils;
import com.fhh.bihu.util.ToastUtil;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText mAccount;
    private TextInputEditText mPassword;
    private TextInputLayout passwordLayout;
    private TextInputLayout accountLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setUpViews();

    }


    private void setUpViews() {
        mAccount = findViewById(R.id.input_account);
        mPassword = findViewById(R.id.input_password);

        Button signUpButton = findViewById(R.id.button_sign_up);
        accountLayout = findViewById(R.id.sign_up_account_layout);
        passwordLayout = findViewById(R.id.sign_up_password_layout);


        signUpButton.setOnClickListener(v -> signUp());

    }


    private void saveAccount() {
        SharedPreferences pref = getSharedPreferences("account", Context.MODE_PRIVATE);
        //保存注册好的账号密码
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("remember_password", true);
        editor.putString("account", mAccount.getText().toString());
        editor.putString("password", mPassword.getText().toString());
        editor.putBoolean("auto_login", false);
        editor.apply();
    }

    private void signUp() {

        if (MyTextUtils.isLegal(mAccount.getText().toString(), 2, 10))
            accountLayout.setErrorEnabled(false);


        if (MyTextUtils.isLegal(mPassword.getText().toString(), 6, 18))
            passwordLayout.setErrorEnabled(false);


        if (MyTextUtils.isLegal(mAccount.getText().toString(), 2, 10)
                && MyTextUtils.isLegal(mPassword.getText().toString(), 6, 18)) {
            accountLayout.setErrorEnabled(false);
            passwordLayout.setErrorEnabled(false);
            String username = mAccount.getText().toString();
            String password = mPassword.getText().toString();

            //实现注册逻辑
            String param = "username=" + username + "&password=" + password;
            HttpUtil.sendHttpRequest(ApiParam.REGISTER, param,
                    new HttpUtil.HttpCallBack() {

                        @Override
                        public void onResponse(HttpUtil.Response response) {
                            if (response.getInfo().equals("success")) {
                                //保存账号密码
                                saveAccount();
                                //跳转到LoginActivity
                                finish();
                            } else {
                                ToastUtil.makeToast(response.getInfo());
                            }
                        }

                        @Override
                        public void onFail(String reason) {
                            if ("参数".equals(reason)) {
                                ToastUtil.makeToast("账号密码有误,登录失败");
                            }
                        }
                    });

        } else {
            if (!MyTextUtils.isLegal(mAccount.getText().toString(), 2, 10)) {
                accountLayout.setErrorEnabled(true);
                accountLayout.setError("用户名不合法");
            }
            if (!MyTextUtils.isLegal(mPassword.getText().toString(), 6, 18)) {
                passwordLayout.setErrorEnabled(true);
                passwordLayout.setError("密码不合法");
            }
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);
    }
}
