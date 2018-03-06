package com.fhh.bihu.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


import com.fhh.bihu.R;
import com.fhh.bihu.adapter.QuestionListRvAdapter;
import com.fhh.bihu.entity.Question;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.ImageUtil;
import com.fhh.bihu.util.JsonParse;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.MyTextUtils;
import com.fhh.bihu.util.ToastUtil;
import com.fhh.bihu.view.MyDialog;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class QuestionListActivity extends AppCompatActivity {


    private List<Question> mQuestionList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private QuestionListRvAdapter adapter;
    private MyDialog dialog;
    private CircleImageView avatar;
    private TextView mUsernameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        ToastUtil.makeToast("欢迎来到逼乎!");
        setUpViews();
        updateQuestions();
    }

    //刷新问题列表数据
    private void updateQuestions() {
        Log.d("刷新时的token", "token = " + MyApplication.getToken());
        swipeRefresh.setRefreshing(true);
        HttpUtil.sendHttpRequest(ApiParam.GET_QUESTION_LIST, "page=0" + "&count=10"
                        + "&token=" + MyApplication.getToken(),
                new HttpUtil.HttpCallBack() {

                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            mQuestionList.clear();//数据请求
                            //Log.d(TAG, "onSuccess: " + data);
                            mQuestionList.addAll(JsonParse.getQuestionList(response.getData()));
                            adapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                        } else {
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


    private void setUpViews() {

        //设置Toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //设置DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        //设置NavigationView

        navigationView = findViewById(R.id.nav_view);
        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        mUsernameTv = view.findViewById(R.id.tv_nav_header_username);
        avatar = view.findViewById(R.id.nav_header_avatar);
        mUsernameTv.setText(MyApplication.getUser().getUsername());
        if (!MyTextUtils.isNull(MyApplication.getUser().getAvatarUrl())) {
            HttpUtil.loadImage(MyApplication.getUser().getAvatarUrl(), (bitmap, info) -> {
                if ("success".equals(info)) avatar.setImageBitmap(bitmap);
                else avatar.setImageResource(R.drawable.nav_icon);
            });
        }
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    drawerLayout.closeDrawers();
                    break;
                case R.id.nav_favorite:
                    drawerLayout.closeDrawers();
                    FavoriteListActivity.actionStart(QuestionListActivity.this);
                    break;
                case R.id.nav_avatar:
                    //跳转到修改头像
                    ChangeAvatarActivity.actionStart(QuestionListActivity.this);
                    break;
                case R.id.nav_password:
                    //修改密码
                    setDialog();
                    break;
                case R.id.nav_logout:
                    logout();
                    LoginActivity.actionStart(QuestionListActivity.this);
                    finish();
                    break;

            }
            return true;
        });


        //设置RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_question_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(QuestionListActivity.this);
        adapter = new QuestionListRvAdapter(mQuestionList, QuestionListRvAdapter.TYPE_HOME);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //设置悬浮按钮
        FloatingActionButton button = findViewById(R.id.fab_ask);
        button.setOnClickListener(v -> AskQuestionActivity.actionStart(QuestionListActivity.this));


        //设置下拉刷新
        swipeRefresh = findViewById(R.id.question_list_swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(this::updateQuestions);

    }


    private void logout() {
        SharedPreferences pref = getSharedPreferences("account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("remember_password", true);
        editor.putString("account", "");
        editor.putString("password", "");
        editor.putBoolean("auto_login", false);
        editor.apply();
    }

    private void setDialog() {
        drawerLayout.closeDrawers();
        dialog = new MyDialog(QuestionListActivity.this);
        //去掉标题
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.setChangePasswordView();
        TextInputEditText first = dialog.getFirstEditText();
        TextInputEditText second = dialog.getSecondEditText();
        TextInputLayout firstLayout = dialog.getFirstLayout();
        TextInputLayout secondLayout = dialog.getSecondLayout();

        dialog.getCancelButton().setOnClickListener(v -> {
            if (dialog.isShowing())
                dialog.dismiss();
        });
        dialog.getYesButton().setOnClickListener(v -> {
            String sFirst = first.getText().toString();
            String sSecond = second.getText().toString();
            if (MyTextUtils.isLegal(sFirst, 6, 18)
                    && MyTextUtils.isLegal(sSecond, 6, 18)
                    && MyTextUtils.isEqual(sFirst, sSecond)) {
                changePassword(sFirst);
            } else {
                if (!MyTextUtils.isLegal(sFirst, 6, 18)) {
                    firstLayout.setErrorEnabled(true);
                    firstLayout.setError("密码不合法");
                } else {
                    firstLayout.setErrorEnabled(false);
                }

                if (!MyTextUtils.isEqual(sFirst, sSecond)) {
                    secondLayout.setErrorEnabled(true);
                    secondLayout.setError("两次密码不相同");
                } else {
                    secondLayout.setErrorEnabled(false);
                }
            }
        });
    }

    private void changePassword(String newPassword) {
        String param = "password=" + newPassword + "&token=" + MyApplication.getToken();
        HttpUtil.sendHttpRequest(ApiParam.CHANGE_PASSWORD, param, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(HttpUtil.Response response) {
                if (response.getInfo().equals("success")) {
                    ToastUtil.makeToast("修改成功");
                    dialog.dismiss();
                    SharedPreferences pref = getSharedPreferences("account", Context.MODE_PRIVATE);
                    //保存注册好的账号密码
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("remember_password", true);
                    editor.putString("account", MyApplication.getUser().getUsername());
                    editor.putString("password", "");
                    editor.putBoolean("auto_login", false);
                    editor.apply();
                    LoginActivity.actionStart(QuestionListActivity.this);
                    finish();
                }
            }

            @Override
            public void onFail(String reason) {
                ToastUtil.makeToast("修改失败,请稍后再试");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_home);
        if (!MyTextUtils.isNull(MyApplication.getUser().getAvatarUrl())) {
            HttpUtil.loadImage(MyApplication.getUser().getAvatarUrl(), (bitmap, info) -> {
                if ("success".equals(info)) avatar.setImageBitmap(bitmap);
                else avatar.setImageResource(R.drawable.nav_icon);
            });
        }
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
