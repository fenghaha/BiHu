package com.fhh.bihu.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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

    private static final String TAG = "FUCKYOU";

    private List<Question> mQuestionList;

    public QuestionListRvAdapter(List<Question> mQuestionList) {
        this.mQuestionList = mQuestionList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView itemAll;
        TextView title;
        TextView authorName;
        TextView content;
        TextView updateTime;
        TextView answerNum;
        TextView excitingNum;
        TextView naiveNum;

        ImageView avatar;
        ImageView comment;
        ImageView exciting;
        ImageView naive;
        ImageView favorite;


        public ViewHolder(View itemView) {
            super(itemView);
            itemAll = (CardView) itemView;
            title = itemView.findViewById(R.id.tv_question_title);
            authorName = itemView.findViewById(R.id.tv_question_authorname);
            content = itemView.findViewById(R.id.tv_question_content);
            updateTime = itemView.findViewById(R.id.tv_question_update_time);
            answerNum = itemView.findViewById(R.id.tv_question_answer_num);
            excitingNum = itemView.findViewById(R.id.tv_question_exciting_num);
            naiveNum = itemView.findViewById(R.id.tv_question_naive_num);

            avatar = itemView.findViewById(R.id.image_question_avatar);
            comment = itemView.findViewById(R.id.image_question_comment);
            exciting = itemView.findViewById(R.id.image_question_exciting);
            naive = itemView.findViewById(R.id.image_question_naive);
            favorite = itemView.findViewById(R.id.image_question_favorite);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        initItemListener(holder);
        Log.d(TAG, "" + holder.getAdapterPosition());
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question question = mQuestionList.get(position);
        holder.title.setText(question.getTitle());
        holder.authorName.setText(question.getAuthorName());
        //TODO 实现头像
        //holder.avatar.setImageURi();
        holder.content.setText(question.getContent());
        holder.updateTime.setText(question.getRecent());
        holder.answerNum.setText(String.valueOf(question.getAnswerCount()));
        holder.excitingNum.setText(String.valueOf(question.getExcitingCount()));
        holder.naiveNum.setText(String.valueOf(question.getNaiveCount()));

        if (!question.isExciting()) {
            holder.exciting.setImageResource(R.drawable.ic_exciting);
        } else {
            holder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
        }
        if (!question.isNaive()) {
            holder.naive.setImageResource(R.drawable.ic_naive);
        } else {
            holder.naive.setImageResource(R.drawable.ic_naive_clicked);
        }
        if (!question.isFavorite()) {
            holder.favorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.favorite.setImageResource(R.drawable.ic_favorite_clicked);
        }
    }

    @Override
    public int getItemCount() {
        return mQuestionList.size();
    }

    private void initItemListener(ViewHolder holder) {

        holder.content.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            //TODO 跳转到问题详情页
        });
        holder.comment.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            //TODO 同上
        });
        holder.exciting.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);

            if (!question.isExciting()) {
                //TODO 网络请求 点赞 成功时把赞数+1
                holder.excitingNum.setText(String.valueOf(Integer.parseInt(holder.excitingNum.getText().toString()) + 1));
                holder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
                question.setExciting(true);
            } else {
                //TODO网络请求  取消赞 成功时把赞数-1
                holder.excitingNum.setText(String.valueOf(Integer.parseInt(holder.excitingNum.getText().toString()) - 1));
                holder.exciting.setImageResource(R.drawable.ic_exciting);
                question.setExciting(false);
            }

        });
        holder.naive.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);

            if (!question.isNaive()) {
                //TODO网络请求  踩 成功时把踩数-1
                holder.naiveNum.setText(String.valueOf(Integer.parseInt(holder.naiveNum.getText().toString()) + 1));
                holder.naive.setImageResource(R.drawable.ic_naive_clicked);
                question.setNaive(true);
            } else {
                //TODO网络请求  踩 成功时把踩数-1
                holder.naiveNum.setText(String.valueOf(Integer.parseInt(holder.naiveNum.getText().toString()) - 1));
                holder.naive.setImageResource(R.drawable.ic_naive);
                question.setNaive(false);
            }
        });
        holder.favorite.setOnClickListener(vv -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            if (!question.isFavorite()) {
                //TODO网络请求  收藏
                holder.favorite.setImageResource(R.drawable.ic_favorite_clicked);
                question.setFavorite(true);
            } else {
                holder.favorite.setImageResource(R.drawable.ic_favorite);
                question.setFavorite(false);
            }
        });
    }
}
