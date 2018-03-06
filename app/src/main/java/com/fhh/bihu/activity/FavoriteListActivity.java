package com.fhh.bihu.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.fhh.bihu.R;
import com.fhh.bihu.adapter.AnswerListRvAdapter;
import com.fhh.bihu.adapter.QuestionListRvAdapter;
import com.fhh.bihu.entity.Question;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.JsonParse;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class FavoriteListActivity extends AppCompatActivity {


    private List<Question> mQuestionList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;
    private QuestionListRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        setUpViews();
    }

    private void setUpViews() {

        //设置Toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //设置RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_question_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FavoriteListActivity.this);
        adapter = new QuestionListRvAdapter(mQuestionList,QuestionListRvAdapter.TYPE_FAVORITE);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        initSwipeRefresh();
    }

    //刷新问题列表数据
    private void updateQuestions() {
        Log.d("刷新时的token", "token = " + MyApplication.getToken());
        AnswerListRvAdapter.needToLoad = true;
        swipeRefresh.setRefreshing(true);
        //数据请求
        HttpUtil.sendHttpRequest(ApiParam.GET_FAVORITE_LIST, "page=0" + "&count=10"
                        + "&token=" + MyApplication.getToken(),
                new HttpUtil.HttpCallBack() {


                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")){
                            mQuestionList.clear();
                            //Log.d(TAG, "onSuccess: " + data);
                            mQuestionList.addAll(JsonParse.getQuestionList(response.getData()));
                            adapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                        }else{
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


    private void initSwipeRefresh() {
        swipeRefresh = findViewById(R.id.question_list_swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(this::updateQuestions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateQuestions();
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

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, FavoriteListActivity.class);
        context.startActivity(intent);
    }
}
