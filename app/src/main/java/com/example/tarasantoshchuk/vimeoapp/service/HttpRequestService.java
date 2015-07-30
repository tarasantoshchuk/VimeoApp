package com.example.tarasantoshchuk.vimeoapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.entity.channel.Channel;
import com.example.tarasantoshchuk.vimeoapp.entity.comment.Comment;
import com.example.tarasantoshchuk.vimeoapp.entity.group.Group;
import com.example.tarasantoshchuk.vimeoapp.entity.user.User;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserList;
import com.example.tarasantoshchuk.vimeoapp.entity.video.Video;
import com.example.tarasantoshchuk.vimeoapp.entity.video.VideoList;
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;
import com.example.tarasantoshchuk.vimeoapp.util.JSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpRequestService extends Service {
    public static final String USER_LIST = "UserList";
    public static final String VIDEO_LIST = "VideoList";
    public static final String GROUP_LIST = "GroupList";
    public static final String CHANNEL_LIST = "ChannelList";
    public static final String COMMENT_LIST = "CommentList";
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String USER = "User";

    public static final String NEXT_PAGE = "NextPage";
    public static final String PREV_PAGE = "PrevPage";

    private static final String HTTP_REQUEST_INFO = "HttpRequestInfo";
    private static final String AUTHORIZATION_CODE = "AuthorizationCode";
    private static final String EXTRAS_TYPE = "ExtrasType";

    private enum ExtrasType {
        HTTP_REQUEST_INFO, AUTHORIZATION_CODE
    }

    private static final String USER_LIST_ACTION = "UserListAction";
    private static final String VIDEO_LIST_ACTION = "VideoListAction";
    private static final String GROUP_LIST_ACTION = "GroupListAction";
    private static final String CHANNEL_LIST_ACTION = "ChannelListAction";
    private static final String COMMENT_LIST_ACTION = "CommentListAction";
    private static final String ACCESS_TOKEN_ACTION = "AccessTokenAction";

    public static IntentFilter getUserListIntentFilter() {
        IntentFilter userListFilter = new IntentFilter();

        userListFilter.addAction(USER_LIST_ACTION);

        return userListFilter;
    }

    public static IntentFilter getVideoListIntentFilter() {
        IntentFilter videoListFilter = new IntentFilter();

        videoListFilter.addAction(VIDEO_LIST_ACTION);

        return videoListFilter;
    }

    public static IntentFilter getGroupListIntentFilter() {
        IntentFilter groupListFilter = new IntentFilter();

        groupListFilter.addAction(GROUP_LIST_ACTION);

        return groupListFilter;
    }

    public static IntentFilter getChannelListIntentFilter() {
        IntentFilter channelListFilter = new IntentFilter();

        channelListFilter.addAction(CHANNEL_LIST_ACTION);

        return channelListFilter;
    }

    public static IntentFilter getCommentListIntentFilter() {
        IntentFilter commentListFilter = new IntentFilter();

        commentListFilter.addAction(COMMENT_LIST_ACTION);

        return commentListFilter;
    }

    public static IntentFilter getAccessTokenIntentFilter() {
        IntentFilter accessTokenFilter = new IntentFilter();

        accessTokenFilter.addAction(ACCESS_TOKEN_ACTION);

        return accessTokenFilter;
    }

    public static Bundle getStartExtras(HttpRequestInfo info) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(EXTRAS_TYPE, ExtrasType.HTTP_REQUEST_INFO);
        bundle.putSerializable(HTTP_REQUEST_INFO, info);

        return bundle;
    }

    public static Bundle getStartExtras(String authCode) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(EXTRAS_TYPE, ExtrasType.AUTHORIZATION_CODE);
        bundle.putString(AUTHORIZATION_CODE, authCode);

        return bundle;
    }

    private ExecutorService mExecutor;

    @Override
    public void onCreate() {
        super.onCreate();

        mExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch((ExtrasType) intent.getSerializableExtra(EXTRAS_TYPE)) {
            case AUTHORIZATION_CODE:
                final String code = intent.getStringExtra(AUTHORIZATION_CODE);
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        RequestManager.getAccessToken(code, ACCESS_TOKEN_ACTION,
                                HttpRequestService.this);
                    }
                });
                break;
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mExecutor.shutdownNow();
    }
}
