package com.fhh.bihu.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fhh.bihu.R;
import com.fhh.bihu.entity.Answer;
import com.fhh.bihu.entity.Question;
import com.fhh.bihu.util.ApiParam;
import com.fhh.bihu.util.HttpUtil;
import com.fhh.bihu.util.ImageUtil;
import com.fhh.bihu.util.JsonParse;
import com.fhh.bihu.util.MyApplication;
import com.fhh.bihu.util.MyTextUtils;
import com.fhh.bihu.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by FengHaHa on 2018/3/1 0001.
 * 回答列表RecyclerView适配器
 */

public class AnswerListRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_QUESTION = 1;
    private static final int TYPE_TAIL = 2;

    private List<Answer> mAnswerList;
    public static boolean needToLoad = true;
    private static Question question;
    private ArrayList<String> imageUrls;

    public AnswerListRvAdapter(List<Answer> answerList, Question question) {
        this.mAnswerList = answerList;
        AnswerListRvAdapter.question = question;
        if (question.getImageUrlStrings() != null) {
            imageUrls = question.getImageUrlStrings();
        }
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView authorName;
        TextView content;
        TextView updateTime;
        TextView answerNum;
        TextView excitingNum;
        TextView naiveNum;
        TextView date;

        ScrollView allImage;

        CircleImageView avatar;
        ImageView comment;
        ImageView exciting;
        ImageView naive;
        ImageView favorite;
        ImageView picture_1;
        ImageView picture_2;
        ImageView picture_3;
        ImageView picture_4;
        ImageView picture_0;
        ArrayList<ImageView> imageViews;

        QuestionViewHolder(View itemView) {
            super(itemView);
            imageViews = new ArrayList<>();

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

            allImage = itemView.findViewById(R.id.scroll_question_image_all);


            picture_0 = itemView.findViewById(R.id.question_image_1);
            picture_1 = itemView.findViewById(R.id.question_image_2);
            picture_2 = itemView.findViewById(R.id.question_image_3);
            picture_3 = itemView.findViewById(R.id.question_image_4);
            picture_4 = itemView.findViewById(R.id.question_image_5);
            imageViews.add(picture_0);
            imageViews.add(picture_1);
            imageViews.add(picture_2);
            imageViews.add(picture_3);
            imageViews.add(picture_4);
        }
    }

    static class NormalViewHolder extends RecyclerView.ViewHolder {


        TextView authorName;
        TextView content;

        TextView excitingNum;
        TextView naiveNum;
        TextView date;

        ImageView avatar;
        ImageView exciting;
        ImageView naive;


        NormalViewHolder(View itemView) {
            super(itemView);

            authorName = itemView.findViewById(R.id.tv_answer_author_name);
            content = itemView.findViewById(R.id.tv_answer_content);
            excitingNum = itemView.findViewById(R.id.tv_answer_exciting_num);
            naiveNum = itemView.findViewById(R.id.tv_answer_naive_num);
            date = itemView.findViewById(R.id.tv_answer_date);

            avatar = itemView.findViewById(R.id.image_answer_avatar);
            exciting = itemView.findViewById(R.id.image_answer_exciting);
            naive = itemView.findViewById(R.id.image_answer_naive);
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_NORMAL:
                NormalViewHolder holder = new NormalViewHolder(inflater.
                        inflate(R.layout.answer_item, parent, false));
                initItemListener(holder, null);
                return holder;

            case TYPE_TAIL:
                return new TailViewHolder(inflater.
                        inflate(R.layout.tail_item, parent, false));
            case TYPE_QUESTION:
                QuestionViewHolder qHolder = new QuestionViewHolder(inflater.
                        inflate(R.layout.question_item, parent, false));
                initItemListener(null, qHolder);
                return qHolder;
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_NORMAL:
                if (mAnswerList.size() > 0) {
                    Answer answer = mAnswerList.get(position - 1);
                    NormalViewHolder normalHolder = (NormalViewHolder) holder;
                    normalHolder.authorName.setText(answer.getAuthorName());
                    normalHolder.date.setText(answer.getDate());
                    normalHolder.content.setText(answer.getContent());
                    normalHolder.excitingNum.setText(String.valueOf(answer.getExcitingCount()));
                    normalHolder.naiveNum.setText(String.valueOf(answer.getNaiveCount()));
                    if (!MyTextUtils.isNull(answer.getAuthorAvatarUrlString())) {
                        HttpUtil.loadImage(answer.getAuthorAvatarUrlString(), (bitmap, info) -> {
                            if ("success".equals(info))
                           normalHolder.avatar.setImageBitmap(bitmap);
                            else normalHolder.avatar.setImageResource(R.drawable.nav_icon);
                        });
                    }
                    if (answer.isExciting()) {
                        normalHolder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
                    } else {
                        normalHolder.exciting.setImageResource(R.drawable.ic_exciting);
                    }

                    if (answer.isNaive()) {
                        normalHolder.naive.setImageResource(R.drawable.ic_naive_clicked);
                    } else {
                        normalHolder.naive.setImageResource(R.drawable.ic_naive);
                    }
                }
                break;
            case TYPE_TAIL:
                TailViewHolder tailViewHolder = (TailViewHolder) holder;
                if (mAnswerList.size() != 0) {
                    if (needToLoad) {
                        String param = "page=" + mAnswerList.size() / 10 + "&count=10" + "&qid="
                                + question.getId() + "&token=" + MyApplication.getToken();
                        loadMore(ApiParam.GET_ANSWER_LIST, param, tailViewHolder);
                    }
                } else {
                    ((TailViewHolder) holder).loadingTextView.setText("精彩回答由你开启");
                }
                break;

            case TYPE_QUESTION:
                QuestionViewHolder qHolder = (QuestionViewHolder) holder;
                if (!MyTextUtils.isNull(question.getAuthorAvatarUrlString())) {
                    HttpUtil.loadImage(question.getAuthorAvatarUrlString(),
                            (bitmap, info) -> {
                                if ("success".equals(info))
                                    qHolder.avatar.setImageBitmap(bitmap);
                                else qHolder.avatar.setImageResource(R.drawable.nav_icon);
                            });
                }
                if (imageUrls != null) {
                    int size = imageUrls.size();
                    Log.d("iiiii", "size = "+size);
                    qHolder.allImage.setVisibility(View.VISIBLE);
                    for (int i = 0; i < size; i++) {
                        int finalI = i;
                        Log.d("iiiii", "i = "+i+"\n url="+imageUrls.get(i));
                        HttpUtil.loadImage(imageUrls.get(i),
                                (bitmap,info) -> {
                                    if ("success".equals(info)){
                                        qHolder.imageViews.get(finalI).setVisibility(View.VISIBLE);
                                        qHolder.imageViews.get(finalI).setImageBitmap(bitmap);
                                    }
                                    else qHolder.imageViews.get(finalI).setVisibility(View.GONE);
                                });
                    }
                }

                qHolder.authorName.setText(question.getAuthorName());
                qHolder.date.setText(question.getDate());
                qHolder.title.setText(question.getTitle());
                qHolder.content.setText(question.getContent());
                qHolder.updateTime.setText(question.getRecent() + "更新");
                qHolder.answerNum.setText(String.valueOf(question.getAnswerCount()));
                qHolder.excitingNum.setText(String.valueOf(question.getExcitingCount()));
                qHolder.naiveNum.setText(String.valueOf(question.getNaiveCount()));


                if (!question.isExciting()) {
                    qHolder.exciting.setImageResource(R.drawable.ic_exciting);
                } else {
                    qHolder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
                }
                if (!question.isNaive()) {
                    qHolder.naive.setImageResource(R.drawable.ic_naive);
                } else {
                    qHolder.naive.setImageResource(R.drawable.ic_naive_clicked);
                }
                if (!question.isFavorite()) {
                    qHolder.favorite.setImageResource(R.drawable.ic_favorite);
                } else {
                    qHolder.favorite.setImageResource(R.drawable.ic_favorite_clicked);
                }
                break;

        }

    }


    @Override
    public int getItemViewType(int position) {
        // Log.d("ItemCoun", "itemcount=" + getItemCount() + "position=" + position);
        if (position == 0)
            return TYPE_QUESTION;
        return position == mAnswerList.size() + 1 ? TYPE_TAIL : TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mAnswerList.size() + 2;//加上loading小尾巴和Question
    }

    private void loadMore(String url, String param, TailViewHolder holder) {
        Log.d("LOADDD", "LoadMore 一次");
        //每次加载10个  当加载出的总数不是10的倍数时  加载完毕
        if (mAnswerList.size() % 10 != 0) {
            holder.loadingTextView.setText("没有更多了");
            needToLoad = false;
            return;
        }

        holder.loadingTextView.setText("加载中...");

        //请求新数据
        HttpUtil.sendHttpRequest(url, param, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(HttpUtil.Response response) {

                if (response.getInfo().equals("success")) {
                    Log.d("getAnswerListSize", "size=: " + JsonParse.getAnswerList(response.getData()).size());
                    if (mAnswerList.size() == Integer.parseInt(JsonParse.getElement
                            (response.getData(), "totalCount"))) {
                        needToLoad = false;
                        holder.loadingTextView.setText("没有更多了");
                    } else if (JsonParse.getAnswerList(response.getData()).size() == 0) {
                        ToastUtil.makeToast("没有更多了???");
                        needToLoad = false;
                        holder.loadingTextView.setText("没有更多了");
                    } else {
                        mAnswerList.addAll(JsonParse.getAnswerList(response.getData()));
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
                needToLoad = false;
            }
        });

    }

    private void initItemListener(NormalViewHolder holder, QuestionViewHolder qHolder) {

        if (holder != null) {
            holder.exciting.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                Answer answer = mAnswerList.get(position - 1);
                if (!answer.isNaive()) {
                    if (!answer.isExciting()) {
                        //网络请求 点赞 成功时把赞数+1
                        addExciting(holder, null, answer);//点赞一次
                    } else {
                        //网络请求  取消赞 成功时把赞数-1
                        cancelExciting(holder, null, answer);//取消点赞
                    }
                } else {
                    //踩后点赞,取消踩
                    cancelNaive(holder, null, answer);
                    addExciting(holder, null, answer);
                }
            });
            holder.naive.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                Answer answer = mAnswerList.get(position - 1);
                if (!answer.isExciting()) {
                    if (!answer.isNaive()) {
                        //网络请求  踩 成功时把踩数-1
                        addNaive(holder, null, answer);
                    } else {
                        //网络请求  踩 成功时把踩数-1
                        cancelNaive(holder, null, answer);
                    }
                } else {
                    //赞后点踩,取消赞
                    cancelExciting(holder, null, answer);
                    addNaive(holder, null, answer);
                }
            });
        } else if (qHolder != null) {
            qHolder.exciting.setOnClickListener(v -> {
                if (!question.isNaive()) {
                    if (!question.isExciting()) {
                        // 网络请求 点赞 成功时把赞数+1
                        addExciting(null, qHolder, null);//点赞一次
                    } else {
                        //网络请求  取消赞 成功时把赞数-1
                        cancelExciting(null, qHolder, null);//取消点赞
                    }
                } else {
                    //踩后点赞,取消踩
                    cancelNaive(null, qHolder, null);
                    addExciting(null, qHolder, null);
                }
            });
            qHolder.naive.setOnClickListener(v -> {
                if (!question.isExciting()) {
                    if (!question.isNaive()) {
                        //TODO网络请求  踩 成功时把踩数-1
                        addNaive(null, qHolder, null);
                    } else {
                        //TODO网络请求  踩 成功时把踩数-1
                        cancelNaive(null, qHolder, null);
                    }
                } else {
                    //赞后点踩,取消赞
                    cancelExciting(null, qHolder, null);
                    addNaive(null, qHolder, null);
                }

            });
            qHolder.favorite.setOnClickListener(v -> {
                qHolder.favorite.setImageResource(R.drawable.ic_favorite_clicked);
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
                    qHolder.favorite.setImageResource(R.drawable.ic_favorite);
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
    }

    private void addExciting(@Nullable NormalViewHolder holder, @Nullable QuestionViewHolder qHolder, @Nullable Answer answer) {
        String param = null;
        if (holder != null && answer != null) {
            holder.excitingNum.setText(String.valueOf(answer.getExcitingCount() + 1));
            holder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
            param = "id=" + answer.getId() + "&type=2" + "&token=" + MyApplication.getToken();
        } else if (qHolder != null) {
            qHolder.excitingNum.setText(String.valueOf(question.getExcitingCount() + 1));
            qHolder.exciting.setImageResource(R.drawable.ic_exciting_clicked);
            param = "id=" + question.getId() + "&type=1" + "&token=" + MyApplication.getToken();
        }

        HttpUtil.sendHttpRequest(ApiParam.ADD_EXCITING, param,
                new HttpUtil.HttpCallBack() {

                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("excited")) {
                            if (answer != null) {
                                answer.setExcitingCount(answer.getExcitingCount() + 1);
                                answer.setExciting(true);
                            } else {
                                question.setExcitingCount(question.getExcitingCount() + 1);
                                question.setExciting(true);
                            }
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

    private void cancelExciting(@Nullable NormalViewHolder holder, QuestionViewHolder qHolder, @Nullable Answer answer) {
        String param = null;
        if (holder != null && answer != null) {
            holder.excitingNum.setText(String.valueOf(answer.getExcitingCount() - 1));
            holder.exciting.setImageResource(R.drawable.ic_exciting);
            param = "id=" + answer.getId() + "&type=2" + "&token=" + MyApplication.getToken();
        } else if (qHolder != null) {
            qHolder.excitingNum.setText(String.valueOf(question.getExcitingCount() - 1));
            qHolder.exciting.setImageResource(R.drawable.ic_exciting);
            param = "id=" + question.getId() + "&type=1" + "&token=" + MyApplication.getToken();
        }

        HttpUtil.sendHttpRequest(ApiParam.CANCEL_EXCITING, param,
                new HttpUtil.HttpCallBack() {

                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            if (answer != null) {
                                answer.setExcitingCount(answer.getExcitingCount() - 1);
                                answer.setExciting(false);
                            } else {
                                question.setExcitingCount(question.getExcitingCount() - 1);
                                question.setExciting(false);
                            }
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

    private void addNaive(@Nullable NormalViewHolder holder, QuestionViewHolder qHolder, @Nullable Answer answer) {
        String param = null;
        if (holder != null && answer != null) {
            holder.naiveNum.setText(String.valueOf(answer.getNaiveCount() + 1));
            holder.naive.setImageResource(R.drawable.ic_naive_clicked);
            param = "id=" + answer.getId() + "&type=2" + "&token=" + MyApplication.getToken();
        } else if (qHolder != null) {
            qHolder.naiveNum.setText(String.valueOf(question.getNaiveCount() + 1));
            qHolder.naive.setImageResource(R.drawable.ic_naive_clicked);
            param = "id=" + question.getId() + "&type=1" + "&token=" + MyApplication.getToken();
        }
        HttpUtil.sendHttpRequest(ApiParam.ADD_NAIVE, param,
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("naive")) {
                            if (answer != null) {
                                answer.setNaiveCount(answer.getNaiveCount() + 1);
                                answer.setNaive(true);
                            } else {
                                question.setNaiveCount(question.getNaiveCount() + 1);
                                question.setNaive(true);
                            }
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

    private void cancelNaive(@Nullable NormalViewHolder holder, QuestionViewHolder qHolder, @Nullable Answer answer) {

        String param = null;
        if (holder != null && answer != null) {
            holder.naiveNum.setText(String.valueOf(answer.getNaiveCount() - 1));
            holder.naive.setImageResource(R.drawable.ic_naive);
            param = "id=" + answer.getId() + "&type=2" + "&token=" + MyApplication.getToken();
        } else if (qHolder != null) {
            qHolder.naiveNum.setText(String.valueOf(question.getNaiveCount() - 1));
            qHolder.naive.setImageResource(R.drawable.ic_naive);
            param = "id=" + question.getId() + "&type=1" + "&token=" + MyApplication.getToken();
        }

        HttpUtil.sendHttpRequest(ApiParam.CANCEL_NAIVE, param,
                new HttpUtil.HttpCallBack() {
                    @Override
                    public void onResponse(HttpUtil.Response response) {
                        if (response.getInfo().equals("success")) {
                            if (answer != null) {
                                answer.setNaiveCount(answer.getNaiveCount() - 1);
                                answer.setNaive(false);
                            } else {
                                question.setNaiveCount(question.getNaiveCount() - 1);
                                question.setNaive(false);
                            }
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
}