package com.fhh.bihu.activity;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;


import com.fhh.bihu.R;
import com.fhh.bihu.adapter.QuestionListRvAdapter;
import com.fhh.bihu.entity.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FXXK";

    private List<Question> mQuestionList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private QuestionListRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();
        initQuestions();

    }

    //初始化问题列表数据
    private void initQuestions() {
        mQuestionList.clear();
        for (int i = 0; i < 10; i++) {
            mQuestionList.add(generateOneQuestion());
        }
    }

    private Question generateOneQuestion() {
        Random random = new Random();
        String sList[] = {"阿斯顿", "撒大功夫", "啊师傅", "当时给我个", "千万人", "共计花费的哈", "阿尔撒大功夫他", "儿童期额他", "艾特我同意",
                "是个读书人以后", "u他说,", "4654654啊虽然", "阿维斯塔", "456问( •̀ ω •́ )y饿因为要", "三等功54153", "啊天然温泉鹅465",
                "asfsfa", "都十分同情而问题", "奇特", "开心", "emoji啊沙发沙发", "喜喜😄"
        };

        Question question = new Question();
        //question.setAuthorAvatarUrlString();
        question.setId(random.nextInt(1000));
        question.setExcitingCount(random.nextInt(20));
        question.setNaiveCount(random.nextInt(20));
        question.setRecent("2018-02-26 23:53");
        question.setAnswerCount(random.nextInt(20));
        int lenth = sList.length;

        question.setTitle(sList[random.nextInt(lenth)]);
        question.setAuthorName(sList[random.nextInt(lenth)]);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            builder.append(sList[random.nextInt(lenth)]);
        }
        question.setContent(builder.toString());

        Log.d(TAG, question.toString());
        return question;

    }

    private void setUpViews() {
        //设置Toolbar
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //设置DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        //设置RecyclerView
        recyclerView = findViewById(R.id.rv_question_list);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        adapter = new QuestionListRvAdapter(mQuestionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        initSwipeRefresh();

    }

    private void initSwipeRefresh() {
        swipeRefresh = findViewById(R.id.question_list_swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(() -> {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    initQuestions();
                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                });
            }).start();

            //TODO  刷新数据
        });
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
