package com.fhh.bihu.util;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.fhh.bihu.entity.User;

/**
 * Created by FengHaHa on 2018/2/24 0024.
 * 全局获取Context
 * 全局获取token
 */

public class MyApplication extends Application {
    private static User mUser;
    private static Context mContext;
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Log.d(TAG, "onCreate: " + mContext.toString());
    }

    public static void setUser(User user) {
        mUser = user;
    }

    public static User getUser() {
        return mUser;
    }

    public static String getToken() {
        if (mUser != null && mUser.getToken() != null) {
            return mUser.getToken();
        }
        return null;
    }

    public static Context getContext() {
        return mContext;
    }
}
