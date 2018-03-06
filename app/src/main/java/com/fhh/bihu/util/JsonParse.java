package com.fhh.bihu.util;

import android.util.Log;

import com.fhh.bihu.entity.Answer;
import com.fhh.bihu.entity.Question;
import com.fhh.bihu.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
            JSONObject userData = new JSONObject(data);
            user.setId(userData.getInt("id"));
            user.setUsername(userData.getString("username"));
            if (userData.has("avatar")) {
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
            JSONObject dataAll = new JSONObject(data);
            JSONArray questionArray = dataAll.getJSONArray("questions");
            for (int i = 0; i < questionArray.length(); i++) {
                JSONObject one = questionArray.getJSONObject(i);
                Question question = new Question();
                question.setId(one.getInt("id"));
                question.setTitle(one.getString("title"));
                question.setContent(one.getString("content"));
                //TODO  图片处理
                if (!MyTextUtils.isNull(one.getString("images"))) {
                    question.setImageUrlStrings(new ArrayList<>
                            (Arrays.asList(one.getString("images").split(","))));

                } else question.setImageUrlStrings(null);
                question.setDate(one.getString("date"));
                if (one.getString("recent").equals("null"))
                    question.setRecent(question.getDate());
                else question.setRecent(one.getString("recent"));
                question.setExcitingCount(one.getInt("exciting"));
                question.setNaiveCount(one.getInt("naive"));
                question.setAnswerCount(one.getInt("answerCount"));
                question.setAuthorId(one.getInt("authorId"));
                question.setAuthorName(one.getString("authorName"));
                question.setAuthorAvatarUrlString(one.getString("authorAvatar"));
                question.setExciting(one.getBoolean("is_exciting"));
                question.setNaive(one.getBoolean("is_naive"));
                if (one.has("is_favorite"))
                    question.setFavorite(one.getBoolean("is_favorite"));
                else question.setFavorite(true);//取收藏列表时json不存在该属性,直接设为true
                //Log.d(TAG, "getQuestionList: " + question.toString());
                questionList.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questionList;
    }

    public static List<Answer> getAnswerList(String content) {
        List<Answer> answerList = new ArrayList<>();
        try {

            JSONObject data = new JSONObject(content);
            JSONArray answerArray = data.getJSONArray("answers");
            for (int i = 0; i < answerArray.length(); i++) {
                Answer answer = new Answer();
                JSONObject ansOne = answerArray.getJSONObject(i);
                answer.setId(ansOne.getInt("id"));
                answer.setContent(ansOne.getString("content"));

                if (!MyTextUtils.isNull(ansOne.getString("images"))) {
                    answer.setImageUrlStrings(new ArrayList<>(Arrays.asList(ansOne.getString("images").split(","))));
                } else answer.setImageUrlStrings(null);
                answer.setDate(ansOne.getString("date"));
                answer.setBestCount(ansOne.getInt("best"));
                answer.setExcitingCount(ansOne.getInt("exciting"));
                answer.setNaiveCount(ansOne.getInt("naive"));
                answer.setAuthorId(ansOne.getInt("authorId"));
                answer.setAuthorName(ansOne.getString("authorName"));
                answer.setAuthorAvatarUrlString(ansOne.getString("authorAvatar"));
                answer.setExciting(ansOne.getBoolean("is_exciting"));
                answer.setNaive(ansOne.getBoolean("is_naive"));
                answerList.add(answer);
                Log.d(TAG, "answer : " + answer.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.makeToast("回答解析异常");
        }
        return answerList;
    }

    public static String getElement(String data, String name) {
        try {
            JSONObject js = new JSONObject(data);
            //检查存在才返回
            if (js.has(name)) {
                return js.getString(name);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("tag", e.toString());
        }
        return null;
    }

}
