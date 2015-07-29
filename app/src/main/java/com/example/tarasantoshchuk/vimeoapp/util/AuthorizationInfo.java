package com.example.tarasantoshchuk.vimeoapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tarasantoshchuk.vimeoapp.R;

public class AuthorizationInfo {
    private static final String SHARED_PREFS_KEY = "com.example.tarasantoshchuk.vimeoapp";
    private static final String ACCESS_TOKEN_KEY = "AccessTokenKey";
    private static final String EMPTY = "";

    private static String sAccessToken;

    private static Context context;

    public static void Init(Context context) {
        AuthorizationInfo.context = context;

        SharedPreferences preferences = context
                .getSharedPreferences(AuthorizationInfo.SHARED_PREFS_KEY, Context.MODE_PRIVATE);

        sAccessToken = preferences.getString(AuthorizationInfo.ACCESS_TOKEN_KEY, EMPTY);
    }

    public static boolean hasAccessToken() {
        return (sAccessToken != null) &&
                !sAccessToken.equals(EMPTY);
    }

    public static String getAccessToken() {
        return sAccessToken;
    }

    public static void setAccessToken(String sAccessToken) {
        AuthorizationInfo.sAccessToken = sAccessToken;

        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS_KEY,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(ACCESS_TOKEN_KEY);
        editor.putString(ACCESS_TOKEN_KEY, sAccessToken);

        editor.commit();
    }
}
