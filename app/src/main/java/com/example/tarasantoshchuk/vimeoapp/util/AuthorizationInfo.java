package com.example.tarasantoshchuk.vimeoapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.tarasantoshchuk.vimeoapp.R;

public class AuthorizationInfo {
    private static final String TAG = AuthorizationInfo.class.getSimpleName();

    private static final String SHARED_PREFS_KEY = "com.example.tarasantoshchuk.vimeoapp";
    private static final String ACCESS_TOKEN_KEY = "AccessTokenKey";

    private static String sAccessToken;

    private static Context context;

    public static void init(Context context) {
        AuthorizationInfo.context = context;

        SharedPreferences preferences = context
                .getSharedPreferences(AuthorizationInfo.SHARED_PREFS_KEY, Context.MODE_PRIVATE);

        sAccessToken = preferences.getString(AuthorizationInfo.ACCESS_TOKEN_KEY, "");
    }

    public static boolean hasAccessToken() {
        Log.d(TAG, "hasAccessToken");

        return (sAccessToken != null) &&
                !TextUtils.isEmpty(sAccessToken);
    }

    public static String getAccessToken() {
        Log.d(TAG, "getAccessToken");
        return sAccessToken;
    }

    public static void setAccessToken(String sAccessToken) {
        Log.d(TAG, "setAccessToken");

        AuthorizationInfo.sAccessToken = sAccessToken;

        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS_KEY,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(ACCESS_TOKEN_KEY);
        editor.putString(ACCESS_TOKEN_KEY, sAccessToken);

        editor.commit();
    }

    public static void clear(Context context) {
        Log.d(TAG, "clear");

        sAccessToken = null;

        SharedPreferences preferences = context
                .getSharedPreferences(AuthorizationInfo.SHARED_PREFS_KEY, Context.MODE_PRIVATE);

        preferences
                .edit()
                .clear()
                .commit();
    }
}
