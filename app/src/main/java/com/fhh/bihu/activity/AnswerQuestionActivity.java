package com.fhh.bihu.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.fhh.bihu.R;
import com.fhh.bihu.entity.Question;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.MyTextUtils;
import com.fhh.bihu.util.ToastUtil;

public class AnswerQuestionActivity extends AppCompatActivity {

    private EditText mContent;
    private Question question;
    private boolean mIsEditStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);
        //接收传递的Question对象
        Intent intent = getIntent();
        question = (Question) intent.getSerializableExtra("question_data");
        setUpViews();

    }

    private void setUpViews() {
        //初始化ToolBar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("回答:" + question.getTitle());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }
        //初始化输入框

        mContent = findViewById(R.id.text_answer_content);
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mIsEditStatus = !MyTextUtils.isEmpty(mContent.getText().toString());
                invalidateOptionsMenu();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsEditStatus = !MyTextUtils.isEmpty(mContent.getText().toString());
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {
                mIsEditStatus = !MyTextUtils.isEmpty(mContent.getText().toString());
                invalidateOptionsMenu();
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
                Log.d("BUsend", "onOptionsItemSelected: send");
                send();
                break;
            case R.id.send_pre:
                Log.d("BUsend", "onOptionsItemSelected: send_pre");
                send();
                break;
        }
        return true;
    }

    private void send() {
        Log.d("回答问题", "回答一次");
        //内容不为空时 发送
        if (!MyTextUtils.isEmpty(mContent.getText().toString())) {
            //TODO 实现上传图片
            String param = "qid=" + question.getId()
                    + "&content=" + mContent.getText().toString()
                    + "&token=" + MyApplication.getToken();
            HttpUtil.sendHttpRequest(ApiParam.ANSWER_A_QUESTION, param, new HttpUtil.HttpCallBack() {
                @Override
                public void onSuccess(String data) {
                    ToastUtil.makeToast("回答成功!");
                    finish();
                }

                @Override
                public void onFail(String reason) {
                    if ("参数".equals(reason))
                        ToastUtil.makeToast("由于网络原因,回答失败");
                }
            });
        } else if (MyTextUtils.isEmpty(mContent.getText().toString())) {
            ToastUtil.makeToast("内容不能为空");
        }
    }

    public static void actionStart(Context context, Question question) {
        Intent intent = new Intent(context, AnswerQuestionActivity.class);
        intent.putExtra("question_data", question);
        context.startActivity(intent);
    }
}
