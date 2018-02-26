package com.fhh.bihu.util;

import android.widget.Toast;

/**
 * Created by FengHaHa on 2018/2/24 0024.
 * 全局弹Toast的工具类
 */

public class ToastUtil {
    public static void makeToast(String content) {
        Toast.makeText(MyApplication.getContext(), content, Toast.LENGTH_SHORT).show();
    }

}
