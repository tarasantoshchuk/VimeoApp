package com.example.tarasantoshchuk.vimeoapp.util;

import android.util.Log;

import com.example.tarasantoshchuk.vimeoapp.entity.user.User;

import org.json.JSONObject;

import java.util.Date;
import java.util.Random;


public class JSONParser {
    private static final String TAG = JSONParser.class.getSimpleName();

    public static String getAccessToken(JSONObject json) {
        Log.d(TAG, "getAccessToken");
        String accessToken = null;
        /**
         * TODO: get access token from json
         */
        return "";
    }

    public static User getUser(JSONObject json) {
        Log.d(TAG, "getUser");
        /**
         * TODO: get user from json
         */
        return new User("id", "name", "UK", "BIO", "https://goo.gl/vbRLx8",
                new Date(System.currentTimeMillis() - new Random().nextInt(999999999)), 0, 0, 0, 0, 0, 0);
    }
}
