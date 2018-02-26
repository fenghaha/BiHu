package com.fhh.bihu.activity;

import android.content.Intent;
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
import com.fhh.bihu.adapter.QuestionListRvAdapter;
import com.fhh.bihu.entity.Question;
import com.fhh.bihu.entity.User;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.JsonParse;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.ToastUtil;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private List<Question> mQuestionList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private NavigationView navigationView;
    private FloatingActionButton button;
    private QuestionListRvAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToastUtil.makeToast("欢迎来到逼乎!");
        MyApplication.setUser(new User());
        setUpViews();
        updateQuestions();
    }

    //刷新问题列表数据
    private void updateQuestions() {
        swipeRefresh.setRefreshing(true);
        HttpUtil.sendHttpRequest(ApiParam.GET_QUESTION_LIST, "page=0" + "&count=10"
                        + "&token=" + MyApplication.getToken(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onSuccess(String data) {
                        mQuestionList.clear();//数据请求
                        //Log.d(TAG, "onSuccess: " + data);
                        mQuestionList.addAll(JsonParse.getQuestionList(data));
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                        for (Question question : mQuestionList) {
                            Log.d("DDDFFF", question.toString());
                        }
                    }

                    @Override
                    public void onFail() {
                        ToastUtil.makeToast("网络错误,刷新失败");
                        swipeRefresh.setRefreshing(false);
                    }
                });
    }


    private void setUpViews() {
        //设置Toolbar
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //设置DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        //设置NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    drawerLayout.closeDrawers();
                    break;
                case R.id.nav_favorite:
                    drawerLayout.closeDrawers();
                    //TODO 跳转到收藏列表   最好弄成fragment
                    break;
                case R.id.nav_avatar:
                    //TODO  跳转到修改头像
                    break;
                case R.id.nav_password:
                    //TODO 修改密码
                    break;
                case R.id.nav_logout:
                    //跳转到登陆界面
                    break;

            }
            return true;
        });

        //设置RecyclerView
        recyclerView = findViewById(R.id.rv_question_list);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        adapter = new QuestionListRvAdapter(mQuestionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //设置浮动按钮
        button = findViewById(R.id.fab_ask);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddQuestionActivity.class);
            startActivity(intent);
        });

        initSwipeRefresh();

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
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
}
