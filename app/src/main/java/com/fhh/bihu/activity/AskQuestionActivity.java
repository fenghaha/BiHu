package com.fhh.bihu.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


import com.fhh.bihu.R;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.MyTextUtils;
import com.fhh.bihu.util.ToastUtil;

public class AskQuestionActivity extends AppCompatActivity {

    private TextInputEditText mTitle;
    private EditText mContent;
    private TextInputLayout mTittleLayout;
    private boolean mIsEditStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        setUpViews();
    }

    private void setUpViews() {
        //初始化ToolBar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }
        //初始化输入框
        mTittleLayout = findViewById(R.id.text_input_layout);
        mTitle = findViewById(R.id.text_question_title);
        mContent = findViewById(R.id.text_question_content);

        //初始化输入框 并设置监听器
        //当输入框类输入文字时,把发送按钮由黑色变成蓝色
        //以提升视觉效果(参考知乎)
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsEditStatus = !(MyTextUtils.isEmpty(mTitle.getText().toString())
                        || MyTextUtils.isEmpty(mContent.getText().toString()));
                invalidateOptionsMenu();
                if (MyTextUtils.isEmpty(mTitle.getText().toString())){
                    mTittleLayout.setErrorEnabled(true);
                    mTittleLayout.setError("标题不能为空");
                }else{
                    mTittleLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsEditStatus = !(MyTextUtils.isEmpty(mTitle.getText().toString())
                        || MyTextUtils.isEmpty(mContent.getText().toString()));
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mIsEditStatus) {
            menu.findItem(R.id.send).setVisible(false);
            menu.findItem(R.id.send_pre).setVisible(true);
        } else {
            menu.findItem(R.id.send).setVisible(true);
            menu.findItem(R.id.send_pre).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.send:
                send();
                break;
            case R.id.send_pre:
                send();
                break;
        }
        return true;
    }

    private void send() {
        //标题和内容都不为空时 发送
        if (!MyTextUtils.isEmpty(mTitle.getText().toString()) &&
                !MyTextUtils.isEmpty(mContent.getText().toString())) {

            mTittleLayout.setErrorEnabled(false);

            //TODO 实现上传图片
            String param = "title=" + mTitle.getText().toString()
                    + "&content=" + mContent.getText().toString()
                    + "&token=" + MyApplication.getToken();
            HttpUtil.sendHttpRequest(ApiParam.ASK_A_QUESTION, param, new HttpUtil.HttpCallBack() {
                @Override
                public void onResponse(HttpUtil.Response response) {
                    if (response.getInfo().equals("success")) {
                        ToastUtil.makeToast("发送成功");
                        finish();
                    } else {
                        ToastUtil.makeToast(response.getInfo());
                    }
                }

                @Override
                public void onFail(String reason) {
                    ToastUtil.makeToast("由于网络原因,发送失败");
                }
            });
        } else {
            if (MyTextUtils.isEmpty(mTitle.getText().toString())) {
                mTittleLayout.setErrorEnabled(true);
                mTittleLayout.setError("标题不能为空!");
            }


            if (MyTextUtils.isEmpty(mContent.getText().toString()))
                ToastUtil.makeToast("内容不能为空");
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AskQuestionActivity.class);
        context.startActivity(intent);
    }
}
