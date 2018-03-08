package com.fhh.bihu.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


import com.fhh.bihu.R;
import com.fhh.bihu.adapter.AnswerListRvAdapter;
import com.fhh.bihu.entity.Answer;
import com.fhh.bihu.entity.Question;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.JsonParse;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class AnswerListActivity extends AppCompatActivity {

    private Question mQuestion;

    private List<Answer> mAnswerList = new ArrayList<>();
    private AnswerListRvAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        //接收传递的Question对象
        Intent intent = getIntent();
        mQuestion = (Question) intent.getSerializableExtra("question_data");

        setUpViews();
        updateAnswers();
    }

    private void setUpViews() {

        //设置Toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle(mQuestion.getTitle());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //设置RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_question_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AnswerListActivity.this);
        adapter = new AnswerListRvAdapter(mAnswerList, mQuestion);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //设置悬浮按钮
        FloatingActionButton button = findViewById(R.id.fab_ask);
        button.setOnClickListener(v -> AnswerQuestionActivity
                .actionStart(AnswerListActivity.this, mQuestion));

        //设置下拉刷新
        swipeRefresh = findViewById(R.id.question_list_swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(this::updateAnswers);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAnswers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void updateAnswers() {
        //刷新后,允许重新加载
        AnswerListRvAdapter.needToLoad = true;
        swipeRefresh.setRefreshing(true);
        HttpUtil.sendHttpRequest(ApiParam.GET_ANSWER_LIST, "page=0" + "&count=10"
                        + "&qid=" + mQuestion.getId() + "&token=" + MyApplication.getToken(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            mAnswerList.clear();//数据请求
                            //Log.d(TAG, "onSuccess: " + data);
                            mAnswerList.addAll(JsonParse.getAnswerList(response.getData()));
                            adapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                        } else {
                            swipeRefresh.setRefreshing(false);
                            ToastUtil.makeToast(response.getInfo());
                        }
                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("网络错误,刷新失败");
                        swipeRefresh.setRefreshing(false);
                    }
                });
    }

    public static void actionStart(Context context, Question question) {
        Intent intent = new Intent(context, AnswerListActivity.class);
        intent.putExtra("question_data", question);
        context.startActivity(intent);
    }

}
