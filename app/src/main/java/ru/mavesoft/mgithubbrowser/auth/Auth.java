package ru.mavesoft.mgithubbrowser.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class Auth {

    private static Auth auth;
    private User user;

    private static Context context;
    private SharedPreferences sharedPreferences;

    private Auth() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPreferences.getString("UserToken", "");

        if (!token.equals("")) {
            user = new User(token);
        }

    }

    public static Auth getInstance(Context context) {
        Auth.context = context;
        if (auth == null) auth = new Auth();
        return auth;
    }

    public User getUser() {
        return user;
    }

    public boolean createAndSaveUser(String token) {
        user = new User(token);
        sharedPreferences.edit().putString("UserToken", token)
                                .apply();

        return true;
    }
}
