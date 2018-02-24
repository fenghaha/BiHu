package com.fhh.bihu.entity;

/**
 * 问题实体类
 * Created by FengHaHa on 2018/2/24 0024.
 */

public class Question {
    private int id;
    private String title;
    private String content;
    private String date;
    private String recent;//最近修改时间
    private int answerCount;
    private int authorId;
    private int excitingCount;
    private int naiveCount;
    private String authorName;
    private String authorAvatarUrlString;
    private boolean isNaive = false;
    private boolean isExciting =false;
    private boolean isFavorite = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRecent() {
        return recent;
    }

    public void setRecent(String recent) {
        this.recent = recent;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getExcitingCount() {
        return excitingCount;
    }

    public void setExcitingCount(int excitingCount) {
        this.excitingCount = excitingCount;
    }

    public int getNaiveCount() {
        return naiveCount;
    }

    public void setNaiveCount(int naiveCount) {
        this.naiveCount = naiveCount;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatarUrlString() {
        return authorAvatarUrlString;
    }

    public void setAuthorAvatarUrlString(String authorAvatarUrlString) {
        this.authorAvatarUrlString = authorAvatarUrlString;
    }

    public boolean isNaive() {
        return isNaive;
    }

    public void setNaive(boolean naive) {
        isNaive = naive;
    }

    public boolean isExciting() {
        return isExciting;
    }

    public void setExciting(boolean exciting) {
        isExciting = exciting;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public String toString() {
        return "id = "+id+"tittle = "+title+"date = "+date+"recent = "+recent+
                "answerCount = "+answerCount+"excitingCount = "+excitingCount+"naiveCount = "+naiveCount+
                "authorName = "+authorName;
    }
}
