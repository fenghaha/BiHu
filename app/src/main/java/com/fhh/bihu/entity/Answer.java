package com.fhh.bihu.entity;

import java.util.ArrayList;

/**
 * Created by FengHaHa on 2018/3/1 0001.
 */

public class Answer {

    private int id;
    private String content;
    private String date;
    private int authorId;
    private int excitingCount;
    private int naiveCount;
    private int bestCount;
    private String authorName;
    private String authorAvatarUrlString;
    private ArrayList<String> imageUrlStrings = new ArrayList<>();
    private boolean isNaive;
    private boolean isExciting;

    public int getBestCount() {
        return bestCount;
    }

    public void setBestCount(int bestCount) {
        this.bestCount = bestCount;
    }

    public ArrayList<String> getImageUrlStrings() {
        return imageUrlStrings;
    }

    public void setImageUrlStrings(ArrayList<String> imageUrlStrings) {
        this.imageUrlStrings = imageUrlStrings;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    @Override
    public String toString() {
        return "id = " + id + "content = " + content + "date = " + date +
                "excitingCount = " + excitingCount + "naiveCount = " + naiveCount +
                "authorName = " + authorName;
    }
}
