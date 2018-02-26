package com.fhh.bihu.activity;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


import com.fhh.bihu.R;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.ToastUtil;

public class AddQuestionActivity extends AppCompatActivity {

    private TextInputEditText mTitle;
    private EditText mContent;
    private TextInputLayout mTittleLayout;

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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.send:
                if (!TextUtils.isEmpty(mTitle.getText().toString()) &&
                        !TextUtils.isEmpty(mContent.getText().toString())) {

                    //TODO 实现上传图片
                    String param = "title=" + mTitle.getText().toString()
                            + "&content=" + mContent.getText().toString()
                            + "&token=" + MyApplication.getToken();
                    HttpUtil.sendHttpRequest(ApiParam.ASK_A_QUESTION, param, new HttpUtil.HttpCallBack() {
                        @Override
                        public void onSuccess(String data) {
                            ToastUtil.makeToast("发送成功");
                            finish();
                        }

                        @Override
                        public void onFail() {
                            ToastUtil.makeToast("由于网络原因,发送失败");
                        }
                    });
                } else {
                    if (TextUtils.isEmpty(mTitle.getText().toString())){
                       mTitle.setError("标题不能为空!");
                    }


                    if (TextUtils.isEmpty(mContent.getText().toString()))
                        ToastUtil.makeToast("内容不能为空");
                }
                break;


        }
        return true;
    }

}
