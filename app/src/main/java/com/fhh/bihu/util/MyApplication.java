package com.fhh.bihu.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by FengHaHa on 2018/2/24 0024.
 * 全局获取Context
 */

public class MyApplication extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
