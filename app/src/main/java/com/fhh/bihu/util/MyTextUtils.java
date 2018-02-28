package com.fhh.bihu.util;

import android.text.TextUtils;

/**
 * Created by FengHaHa on 2018/2/27 0027.
 * 判断文本是否合法
 * 不能为空(null , "" ,或者全空格 ),防止发出全是空格的内容
 * 也可限定长度
 */

public class MyTextUtils {
    public static boolean isLegal(String text, int minLenth, int maxLEnth) {

        //为空
        if (TextUtils.isEmpty(text) || isAllSpace(text)) {
            return false;
        } else if (text.length() >= minLenth && text.length() <= maxLEnth) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(String text) {
        return TextUtils.isEmpty(text) || isAllSpace(text);
    }

    private static boolean isAllSpace(String text) {
        char temp[] = text.toCharArray();
        for (char aTemp : temp) {
            if (aTemp != ' ') return false;
        }
        return true;
    }
}