package com.fhh.bihu.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fhh.bihu.R;
import com.fhh.bihu.activity.AnswerListActivity;
import com.fhh.bihu.activity.AnswerQuestionActivity;
import com.fhh.bihu.entity.Question;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.DateUtil;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.ImageUtil;
import com.fhh.bihu.util.JsonParse;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.MyTextUtils;
import com.fhh.bihu.util.ToastUtil;


import java.util.List;

/**
 * 问题列表RecyclerView适配器
 * Created by FengHaHa on 2018/2/24 0024.
 */

public class QuestionListRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "QuestionListRvAdapter";

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_TAIL = 1;
    public static final int TYPE_HOME = 0;
    public static final int TYPE_FAVORITE = 1;

    public static boolean isLoading = false;
    private int questionType;

    private List<Question> mQuestionList;

    public QuestionListRvAdapter(List<Question> mQuestionList, int type) {
        this.mQuestionList = mQuestionList;
        questionType = type;
    }

    static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView authorName;
        TextView content;
        TextView updateTime;
        TextView answerNum;
        TextView excitingNum;
        TextView naiveNum;
        TextView date;

        LinearLayout forClick;

        ImageView avatar;
        ImageView comment;
        ImageView exciting;
        ImageView naive;
        ImageView favorite;
        ImageView imagePre;


        NormalViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_question_title);
            authorName = itemView.findViewById(R.id.tv_question_authorname);
            content = itemView.findViewById(R.id.tv_question_content);
            updateTime = itemView.findViewById(R.id.tv_question_update_time);
            answerNum = itemView.findViewById(R.id.tv_question_answer_num);
            excitingNum = itemView.findViewById(R.id.tv_question_exciting_num);
            naiveNum = itemView.findViewById(R.id.tv_question_naive_num);
            date = itemView.findViewById(R.id.tv_ask_date);
            forClick = itemView.findViewById(R.id.layout_to_click);

            avatar = itemView.findViewById(R.id.image_question_avatar);
            comment = itemView.findViewById(R.id.image_question_comment);
            exciting = itemView.findViewById(R.id.image_question_exciting);
            naive = itemView.findViewById(R.id.image_question_naive);
            favorite = itemView.findViewById(R.id.image_question_favorite);
            imagePre = itemView.findViewById(R.id.image_preview);
        }
    }

    static class TailViewHolder extends RecyclerView.ViewHolder {

        private TextView loadingTextView;

        TailViewHolder(View itemView) {
            super(itemView);
            loadingTextView = itemView.findViewById(R.id.tv_loading_tail);
        }
    }


    @Override
    public int getItemViewType(int position) {
        // Log.d("ItemCoun", "itemcount=" + getItemCount() + "position=" + position);
        return position == mQuestionList.size() ? TYPE_TAIL : TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_NORMAL:
                NormalViewHolder holder = new NormalViewHolder(inflater.
                        inflate(R.layout.question_item, parent, false));
                initItemListener(holder);
                return holder;
            case TYPE_TAIL:
                return new TailViewHolder(inflater.inflate(R.layout.tail_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_NORMAL:
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                Question question = mQuestionList.get(position);
                normalViewHolder.title.setText(question.getTitle());
                normalViewHolder.authorName.setText(question.getAuthorName());
                Log.d(TAG, "position: " + position);
                if (!MyTextUtils.isNull(question.getAuthorAvatarUrlString())) {

                    Log.d(TAG, "第 " + position + "用户名" + question.getAuthorName() + "头像不为空"
                            + "url=" + question.getAuthorAvatarUrlString());
                    HttpUtil.loadImage(question.getAuthorAvatarUrlString(),
                            (Bitmap bitmap, String info) -> {
                                if ("success".equals(info))
                                    normalViewHolder.avatar.setImageBitmap(bitmap);
                                else normalViewHolder.avatar.setImageResource(R.drawable.nav_icon);
                            });

                } else {
                    Log.d(TAG, "第 " + position + "用户名" + question.getAuthorName() + "头像为空");
                    normalViewHolder.avatar.setImageResource(R.drawable.nav_icon);
                }
                if (question.getImageUrlStrings() != null) {
                    Log.d(TAG, "第 " + position + "用户名" + question.getAuthorName() + "图片不为空"
                            + "url=" + question.getImageUrlStrings().get(0));
                    String url = question.getImageUrlStrings().get(0);
                    HttpUtil.loadImage(url, (bitmap, info) -> {
                        if ("success".equals(info)) {
                            normalViewHolder.imagePre.setImageBitmap(bitmap);
                            normalViewHolder.imagePre.setBackgroundResource(R.drawable.bg_dialog);
                            normalViewHolder.imagePre.setVisibility(View.VISIBLE);
                        } else normalViewHolder.imagePre.setVisibility(View.GONE);
                    });
                } else {
                    normalViewHolder.imagePre.setVisibility(View.GONE);
                }

                normalViewHolder.content.setText(question.getContent());
                // Log.d("getRecent", question.getRecent());
                normalViewHolder.date.setText("发布于" + DateUtil.getTime(question.getDate()));
                normalViewHolder.updateTime.setText(DateUtil.getTime(question.getRecent()) + "更新");
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
                break;

            case TYPE_TAIL:

                String param = "page=" + mQuestionList.size() / 10 + "&count=10"
                        + "&token=" + MyApplication.getToken();
                TailViewHolder tailViewHolder = (TailViewHolder) holder;

                String url = (questionType == TYPE_HOME ?
                        ApiParam.GET_QUESTION_LIST : ApiParam.GET_FAVORITE_LIST);
                loadMore(url, param, tailViewHolder);

                break;
        }

    }

    @Override
    public int getItemCount() {
        return mQuestionList.size() + 1;//加上loading小尾巴
    }

    private void addExciting(NormalViewHolder holder, Question question) {


        holder.excitingNum.setText(String.valueOf(question.getExcitingCount() + 1));
        holder.exciting.setImageResource(R.drawable.ic_exciting_clicked);

        HttpUtil.sendHttpRequest(ApiParam.ADD_EXCITING, "id=" + question.getId() + "&type=1"
                        + "&token=" + MyApplication.getToken(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("excited")) {
                            question.setExcitingCount(question.getExcitingCount() + 1);
                            question.setExciting(true);
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }

                    }

                    @Override
                    public void onFail(String reason) {
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
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            question.setExcitingCount(question.getExcitingCount() - 1);
                            question.setExciting(false);
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }

                    }

                    @Override
                    public void onFail(String reason) {
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
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("naive")) {
                            question.setNaiveCount(question.getNaiveCount() + 1);
                            question.setNaive(true);
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }

                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("你点得太快,网络跟不上了哦");
                    }
                });

    }

    private void cancelNaive(NormalViewHolder holder, Question question) {

        holder.naiveNum.setText(String.valueOf(question.getNaiveCount() - 1));
        holder.naive.setImageResource(R.drawable.ic_naive);
        HttpUtil.sendHttpRequest(ApiParam.CANCEL_NAIVE, "id=" + question.getId() + "&type=1"
                        + "&token=" + MyApplication.getToken(),
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            question.setNaiveCount(question.getNaiveCount() - 1);
                            question.setNaive(false);
                        } else {
                            ToastUtil.makeToast(response.getInfo());
                        }

                    }

                    @Override
                    public void onFail(String reason) {
                        ToastUtil.makeToast("你点得太快,网络跟不上了哦");
                    }
                });

    }

    private void initItemListener(NormalViewHolder holder) {
        holder.content.setLines(3);
        holder.forClick.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            //跳转到问题详情页
            AnswerListActivity.actionStart(holder.answerNum.getContext(), question);
        });
        holder.comment.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            Log.d("Intent_que", question.toString());
            AnswerQuestionActivity.actionStart(v.getContext(), question);
        });
        holder.exciting.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);
            if (!question.isNaive()) {
                if (!question.isExciting()) {
                    // 网络请求 点赞 成功时把赞数+1
                    addExciting(holder, question);//点赞一次
                } else {
                    //网络请求  取消赞 成功时把赞数-1
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
                    //网络请求  踩 成功时把踩数-1
                    addNaive(holder, question);
                } else {
                    //网络请求  踩 成功时把踩数-1
                    cancelNaive(holder, question);
                }
            } else {
                //赞后点踩,取消赞
                cancelExciting(holder, question);
                addNaive(holder, question);
            }

        });
        holder.favorite.setOnClickListener(v -> {

            int position = holder.getAdapterPosition();
            Question question = mQuestionList.get(position);

            holder.favorite.setImageResource(R.drawable.ic_favorite_clicked);
            if (!question.isFavorite()) {
                //网络请求  收藏
                HttpUtil.sendHttpRequest(ApiParam.ADD_FAVORITE, "qid=" + question.getId()
                                + "&token=" + MyApplication.getToken(),
                        new HttpUtil.HttpCallBack() {
                            @Override
                            public void onResponse(HttpUtil.Response response) {
                                if (response.getInfo().equals("success")) {
                                    question.setFavorite(true);
                                } else {
                                    ToastUtil.makeToast(response.getInfo());
                                }

                            }

                            @Override
                            public void onFail(String reason) {
                                ToastUtil.makeToast("你点得太快,网络跟不上了哦");
                            }
                        });
            } else {
                holder.favorite.setImageResource(R.drawable.ic_favorite);
                HttpUtil.sendHttpRequest(ApiParam.CANCEL_FAVORITE, "qid=" + question.getId()
                                + "&token=" + MyApplication.getToken(),
                        new HttpUtil.HttpCallBack() {
                            @Override
                            public void onResponse(HttpUtil.Response response) {
                                if (response.getInfo().equals("success")) {
                                    question.setFavorite(false);
                                } else {
                                    ToastUtil.makeToast(response.getInfo());
                                }

                            }

                            @Override
                            public void onFail(String reason) {
                                ToastUtil.makeToast("你点得太快,网络跟不上了哦");
                            }
                        });

            }


        });
    }

    private void loadMore(String url, String param, TailViewHolder holder) {
        if (questionType == TYPE_FAVORITE)
            Log.d("ISLODEMORE?", "收藏列表加载一次");
        isLoading = true;
        //每次加载10个  当加载出的总数不是10的倍数时  加载完毕
        if (mQuestionList.size() % 10 != 0) {
            holder.loadingTextView.setText("没有更多了  :)");
            isLoading = false;
            return;
        }


        holder.loadingTextView.setText("加载中...");
        if (questionType == TYPE_FAVORITE && isLoading){
            holder.loadingTextView.setText("你还没有收藏哦  :)");
            isLoading = false;
            return;
        }


        //请求新数据
        HttpUtil.sendHttpRequest(url, param, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(HttpUtil.Response response) {
                Log.d("DATAonResponse: ", "onResponse: " + response.getData() + "que:" + JsonParse.getElement(response.getData(), "questions"));
                if (response.getInfo().equals("success")) {
                    if (mQuestionList.size() == Integer.parseInt(JsonParse.getElement
                            (response.getData(), "totalCount"))) {
                        ToastUtil.makeToast("没有更多了###");
                        isLoading = false;
                        holder.loadingTextView.setText("没有更多了  :)");
                    }
                    if (JsonParse.getElement(response.getData(), "questions").equals("[]")) {
                        ToastUtil.makeToast("没有更多了???");
                        isLoading = false;
                        holder.loadingTextView.setText("没有更多了 :)");
                    } else {
                        mQuestionList.addAll(JsonParse.getQuestionList(response.getData()));

                        notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.makeToast(response.getInfo());
                }
            }

            @Override
            public void onFail(String reason) {
                holder.loadingTextView.setText("加载失败");
                ToastUtil.makeToast("加载失败,请稍后再试");
                isLoading = false;
            }
        });
    }
}
