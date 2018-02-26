package com.fhh.bihu.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fhh.bihu.R;
import com.fhh.bihu.entity.Question;
import com.fhh.bihu.entity.User;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.JsonParse;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.ToastUtil;


import java.util.List;

/**
 * 问题列表RecyclerView适配器
 * Created by FengHaHa on 2018/2/24 0024.
 */

public class QuestionListRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_TAIL = 1;
    private List<Question> mQuestionList;

    public QuestionListRvAdapter(List<Question> mQuestionList) {
        this.mQuestionList = mQuestionList;
    }

    static class NormalViewHolder extends RecyclerView.ViewHolder {

        CardView itemAll;
        TextView title;
        TextView authorName;
        TextView content;
        TextView updateTime;
        TextView answerNum;
        TextView excitingNum;
        TextView naiveNum;
        TextView date;

        ImageView avatar;
        ImageView comment;
        ImageView exciting;
        ImageView naive;
        ImageView favorite;


        public NormalViewHolder(View itemView) {
            super(itemView);
            itemAll = (CardView) itemView;
            title = itemView.findViewById(R.id.tv_question_title);
            authorName = itemView.findViewById(R.id.tv_question_authorname);
            content = itemView.findViewById(R.id.tv_question_content);
            updateTime = itemView.findViewById(R.id.tv_question_update_time);
            answerNum = itemView.findViewById(R.id.tv_question_answer_num);
            excitingNum = itemView.findViewById(R.id.tv_question_exciting_num);
            naiveNum = itemView.findViewById(R.id.tv_question_naive_num);
            date = itemView.findViewById(R.id.tv_ask_date);

            avatar = itemView.findViewById(R.id.image_question_avatar);
            comment = itemView.findViewById(R.id.image_question_comment);
            exciting = itemView.findViewById(R.id.image_question_exciting);
            naive = itemView.findViewById(R.id.image_question_naive);
            favorite = itemView.findViewById(R.id.image_question_favorite);

        }
    }

    static class TailViewHolder extends RecyclerView.ViewHolder {

        private TextView loadingTextView;

        public TailViewHolder(View itemView) {
            super(itemView);
            loadingTextView = itemView.findViewById(R.id.tv_loading_tail);
        }
    }

    @Override
    public int getItemViewType(int position) {
       // Log.d("ItemCoun", "itemcount=" + getItemCount() + "position=" + position);
        return position == getItemCount() - 1 ? TYPE_TAIL : TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //user.setToken("4642a17dd03233682abf41c3d823cdcd9ff102d6");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_NORMAL:
                NormalViewHolder holder = new NormalViewHolder(inflater.
                        inflate(R.layout.question_item, parent, false));
                initItemListener(holder);
                return holder;
            case TYPE_TAIL:
                TailViewHolder holder1 = new TailViewHolder(inflater.
                        inflate(R.layout.tail_item, parent, false));
                return holder1;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_NORMAL: {
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                Question question = mQuestionList.get(position);
                normalViewHolder.title.setText(question.getTitle());
                normalViewHolder.authorName.setText(question.getAuthorName());
                //TODO 实现头像
                //holder.avatar.setImageURi();
                normalViewHolder.content.setText(question.getContent());
                // Log.d("getRecent", question.getRecent());
                normalViewHolder.date.setText("发布于"+question.getDate());
                if ("null".equals(question.getRecent())) {
                    normalViewHolder.updateTime.setText(question.getDate() + "提问");
                } else {
                    normalViewHolder.updateTime.setText(question.getRecent() + "更新");
                }

                normalViewHolder.answerNum.setText(String.valueOf(question.getAnswerCount()));
                normalViewHolder.excitingNum.setText(String.valueOf(question.getExcitingCount()));
                normalViewHolder.naiveNum.setText(String.valueOf(question.getNaiveCount()));

                if (!question.isExciting()) {
                    normalViewHolder.exciting.setImageResource(R.drawable.ic_exciting);
                } else {
                    normalViewHolder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
                }
                if (!question.isNaive()) {
                    normalViewHolder.naive.setImageResource(R.drawable.ic_naive);
                } else {
                    normalViewHolder.naive.setImageResource(R.drawable.ic_naive_clicked);
                }
                if (!question.isFavorite()) {
                    normalViewHolder.favorite.setImageResource(R.drawable.ic_favorite);
                } else {
                    normalViewHolder.favorite.setImageResource(R.drawable.ic_favorite_clicked);
                }
            }
            break;

            //TODO 把token改好
            case TYPE_TAIL:
                Log.d("4444", "token = " + MyApplication.getToken());
                String param = "page=" + mQuestionList.size() / 10 + "&count=10" + "&token=" + MyApplication.getToken();
                TailViewHolder tailViewHolder = (TailViewHolder) holder;
                loadMore(ApiParam.GET_QUESTION_LIST, param, tailViewHolder);

                break;
        }

    }

    @Override
    public int getItemCount() {
        return mQuestionList.size() + 1;//加上loading小尾巴
    }

    //点赞
    private void addExciting(NormalViewHolder holder, Question question) {


        holder.excitingNum.setText(String.valueOf(question.getExcitingCount() + 1));
        holder.exciting.setImageResource(R.drawable.ic_exciting_clicked);

        HttpUtil.sendHttpRequest(ApiParam.ADD_EXCITING, "id=" + question.getId() + "&type=1"
                        + "&token=" + MyApplication.getToken(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onSuccess(String data) {
                        question.setExcitingCount(question.getExcitingCount() + 1);
                        question.setExciting(true);
                    }

                    @Override
                    public void onFail() {
                        ToastUtil.makeToast("你点得太快,网络跟不上了哦");
                    }
                });

    }

    private void cancelExciting(NormalViewHolder holder, Question question) {

        holder.excitingNum.setText(String.valueOf(question.getExcitingCount() - 1));
        holder.exciting.setImageResource(R.drawable.ic_exciting);
        HttpUtil.sendHttpRequest(ApiParam.CANCEL_EXCITING, "id=" + question.getId() + "&type=1" +
                        "&token=" + MyApplication.getToken(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onSuccess(String data) {
                        question.setExcitingCount(question.getExcitingCount() - 1);
                        question.setExciting(false);
                    }

                    @Override
                    public void onFail() {
                        ToastUtil.makeToast("你点得太快,网络跟不上了哦");
                    }
                });

    }

    private void addNaive(NormalViewHolder holder, Question question) {

        holder.naiveNum.setText(String.valueOf(question.getNaiveCount() + 1));
        holder.naive.setImageResource(R.drawable.ic_naive_clicked);
        HttpUtil.sendHttpRequest(ApiParam.ADD_NAIVE, "id=" + question.getId() + "&type=1" +
                        "&token=" + MyApplication.getToken(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onSuccess(String data) {

                        question.setNaiveCount(question.getNaiveCount() + 1);
                        question.setNaive(true);
                    }

                    @Override
                    public void onFail() {
                        ToastUtil.makeToast("你点得太快,网络跟不上了哦");
                    }
                });

    }

    private void cancelNaive(NormalViewHolder holder, Question question) {

        holder.naiveNum.setText(String.valueOf(Integer.parseInt(holder.naiveNum.getText().toString()) - 1));
        holder.naive.setImageResource(R.drawable.ic_naive);
        HttpUtil.sendHttpRequest(ApiParam.CANCEL_NAIVE, "id=" + question.getId() + "&type=1"
                        + "&token=" + MyApplication.getToken(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onSuccess(String data) {

                        question.setNaiveCount(question.getNaiveCount() - 1);
                        question.setNaive(false);
                    }

                    @Override
                    public void onFail() {
                        ToastUtil.makeToast("你点得太快,网络跟不上了哦");
                    }
                });

    }

    private void initItemListener(NormalViewHolder holder) {
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
            if (!question.isNaive()) {
                if (!question.isExciting()) {
                    //TODO 网络请求 点赞 成功时把赞数+1
                    addExciting(holder, question);//点赞一次
                } else {
                    //TODO网络请求  取消赞 成功时把赞数-1
                    cancelExciting(holder, question);//取消点赞
                }
            } else {
                //踩后点赞,取消踩
                cancelNaive(holder, question);
                addExciting(holder, question);
            }

        });
        holder.naive.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            if (!question.isExciting()) {
                if (!question.isNaive()) {
                    //TODO网络请求  踩 成功时把踩数-1
                    addNaive(holder, question);
                } else {
                    //TODO网络请求  踩 成功时把踩数-1
                    cancelNaive(holder, question);
                }
            } else {
                //赞后点踩,取消赞
                cancelExciting(holder, question);
                addNaive(holder, question);
            }

        });
        holder.favorite.setOnClickListener(vv -> {

            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);

            holder.favorite.setImageResource(R.drawable.ic_favorite_clicked);
            if (!question.isFavorite()) {
                //网络请求  收藏
                HttpUtil.sendHttpRequest(ApiParam.ADD_FAVORITE, "qid=" + question.getId()
                                + "&token=" + MyApplication.getToken(),
                        new HttpUtil.HttpCallBack() {
                            @Override
                            public void onSuccess(String data) {
                                question.setFavorite(true);
                            }

                            @Override
                            public void onFail() {
                                ToastUtil.makeToast("你点得太快,网络跟不上了哦");
                            }
                        });
            } else {
                holder.favorite.setImageResource(R.drawable.ic_favorite);
                HttpUtil.sendHttpRequest(ApiParam.CANCEL_FAVORITE, "qid=" + question.getId()
                                + "&token=" + MyApplication.getToken(),
                        new HttpUtil.HttpCallBack() {
                            @Override
                            public void onSuccess(String data) {
                                question.setFavorite(false);
                            }

                            @Override
                            public void onFail() {
                                ToastUtil.makeToast("你点得太快,网络跟不上了哦");
                            }
                        });

            }


        });
    }

    private void loadMore(String url, String param, TailViewHolder holder) {
        Log.d("ISLODEMORE?", "已经loadMore了哦哦哦哦哦哦 ");
        //每次加载10个  当加载出的总数不是10的倍数时  加载完毕
        if ((getItemCount() - 1) % 10 != 0) {
            holder.loadingTextView.setText("没有更多了");
            return;
        }

        holder.loadingTextView.setText("加载中...");

        //请求新数据
        HttpUtil.sendHttpRequest(url, param, new HttpUtil.HttpCallBack() {
            @Override
            public void onSuccess(String data) {
                Log.d("LOADING", data);
                if (data == null || data.equals("null") || data.equals("[]")) {
                    ToastUtil.makeToast("没有更多了");
                    holder.loadingTextView.setText("没有更多了");
                    return;
                }

                mQuestionList.addAll(JsonParse.getQuestionList(data));

                notifyDataSetChanged();
            }

            @Override
            public void onFail() {
                holder.loadingTextView.setText("加载失败");
                ToastUtil.makeToast("加载失败,请稍后再试");
            }
        });
    }
}
