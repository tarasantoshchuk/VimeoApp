package com.example.tarasantoshchuk.vimeoapp.util;

import android.util.Log;

import com.example.tarasantoshchuk.vimeoapp.entity.channel.Channel;
import com.example.tarasantoshchuk.vimeoapp.entity.comment.Comment;
import com.example.tarasantoshchuk.vimeoapp.entity.group.Group;
import com.example.tarasantoshchuk.vimeoapp.entity.user.User;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserList;
import com.example.tarasantoshchuk.vimeoapp.entity.video.Video;
import com.example.tarasantoshchuk.vimeoapp.entity.video.VideoList;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestInfo;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class JSONParser {
    private static final String TAG = JSONParser.class.getSimpleName();

    private static final String ACCESS_TOKEN_KEY = "access_token";

    public static String getAccessToken(JSONObject json) {
        Log.d(TAG, "getAccessToken");

        String accessToken;
        try {
            accessToken = json.getString(ACCESS_TOKEN_KEY);
        } catch (JSONException e) {
            Log.e(TAG, "getAccessToken: exception", e);
            throw new RuntimeException(e);
        }
        return accessToken;
    }

    public static User getUser(JSONObject json) {
        Log.d(TAG, "getUser");
        return null;
    }

    public static UserList getUserList(JSONObject json) {
       return null;
    }

    public static Video getVideo(JSONObject json) {
        return null;
    }

    public static VideoList getVideoList(JSONObject json) {
        return null;
    }

    public static Channel getChannel(JSONObject json) {
        return null;
    }

    public static ArrayList<Channel> getChannelList(JSONObject json) {
        return null;
    }

    public static Group getGroup(JSONObject json) {
        return null;
    }

    public static ArrayList<Group> getGroupList(JSONObject json) {
        return null;
    }

    public static ArrayList<Comment> getCommentList(JSONObject json) {
        return null;
    }

    public static HttpRequestInfo getNextPage(JSONObject json) {
        return null;
    }

    public static HttpRequestInfo getPrevPage(JSONObject json) {
        return null;
    }
}
