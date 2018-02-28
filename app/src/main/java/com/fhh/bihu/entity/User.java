package com.fhh.bihu.entity;

/**
 * Created by FengHaHa on 2018/2/25 0025.
 * 用户实体类
 */

public class User {

    private int id;
    private String username;
    private String avatarUrl;
    private String token;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
