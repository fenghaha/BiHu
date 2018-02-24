package com.fhh.bihu.util;

import android.widget.Toast;

/**
 * Created by FengHaHa on 2018/2/24 0024.
 * 全局弹Toast的工具类
 */

public class ToastUtil {
    public static void showError(String error) {
        Toast.makeText(MyApplication.getContext(), error, Toast.LENGTH_LONG).show();
    }

    public static void showHint(String result) {
        Toast.makeText(MyApplication.getContext(), result, Toast.LENGTH_SHORT).show();
    }
}
