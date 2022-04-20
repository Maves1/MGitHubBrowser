package ru.mavesoft.mgithubbrowser.auth;

import com.google.gson.annotations.SerializedName;

public class User {

    private String token;

    public User(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
