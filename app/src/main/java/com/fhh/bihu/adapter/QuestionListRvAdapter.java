package com.fhh.bihu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fhh.bihu.R;
import com.fhh.bihu.entity.Question;


import java.util.List;

/**
 * 问题列表RecyclerView适配器
 * Created by FengHaHa on 2018/2/24 0024.
 */

public class QuestionListRvAdapter extends RecyclerView.Adapter<QuestionListRvAdapter.ViewHolder> {

    private List<Question> mQuestionList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView username;
        TextView content;
        TextView updateTime;
        TextView commentNum;
        TextView excitedNum;
        TextView naiveNum;

        ImageView avatar;
        ImageView comment;
        ImageView exciting;


        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
