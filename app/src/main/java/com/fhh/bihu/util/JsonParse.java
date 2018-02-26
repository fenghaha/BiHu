package com.fhh.bihu.util;

import android.util.Log;

import com.fhh.bihu.entity.Question;
import com.fhh.bihu.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FengHaHa on 2018/2/25 0025.
 * Json解析类
 */

public class JsonParse {
    private static final String TAG = "FHH123";

    public static User getUser(String data) {
        User user = new User();
        try {
            JSONObject json = new JSONObject(data);
            JSONObject userData = json.getJSONObject("data");
            user.setId(userData.getInt("id"));
            user.setUsername(userData.getString("username"));
            if (userData.getString("avatar") != null) {
                user.setAvatarUrl(userData.getString("avatar"));
            }
            user.setToken(userData.getString("token"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static List<Question> getQuestionList(String data) {
        List<Question> questionList = new ArrayList<>();
        try {
            JSONObject js = new JSONObject(data);
            JSONObject dataAll = js.getJSONObject("data");
            JSONArray questionArray = dataAll.getJSONArray("questions");
            for (int i = 0; i < questionArray.length(); i++) {
                JSONObject one = questionArray.getJSONObject(i);
                Question question = new Question();
                question.setId(one.getInt("id"));
                question.setTitle(one.getString("title"));
                question.setContent(one.getString("content"));
                //TODO  图片处理  question.setima
                question.setDate(one.getString("date"));
                question.setExcitingCount(one.getInt("exciting"));
                question.setNaiveCount(one.getInt("naive"));
                question.setRecent(one.getString("recent"));
                question.setAnswerCount(one.getInt("answerCount"));
                question.setAuthorId(one.getInt("authorId"));
                question.setAuthorName(one.getString("authorName"));
                question.setAuthorAvatarUrlString(one.getString("authorAvatar"));
                question.setExciting(one.getBoolean("is_exciting"));
                question.setNaive(one.getBoolean("is_naive"));
                question.setFavorite(one.getBoolean("is_favorite"));

                Log.d(TAG, "getQuestionList: " + question.toString());
                questionList.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questionList;
    }

}
