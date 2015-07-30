package com.example.tarasantoshchuk.vimeoapp.util;

import com.example.tarasantoshchuk.vimeoapp.entity.user.User;

import org.json.JSONObject;

import java.util.Date;
import java.util.Random;


public class JSONParser {

    public static String getAccessToken(JSONObject json) {
        String accessToken = null;
        /**
         * TODO: get access token from json
         */
        return "";
    }

    public static User getUser(JSONObject json) {
        /**
         * TODO: get user from json
         */
        return new User("id", "name", "UK", "BIO", "https://goo.gl/vbRLx8",
                new Date(System.currentTimeMillis() - new Random().nextInt(999999999)), 10, 100, 1000, 1, 3, 29);
    }
}
