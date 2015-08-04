package com.example.tarasantoshchuk.vimeoapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

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
    public static final String CHANNEL = "Channel";
    public static final String GROUP = "Group";
    public static final String VIDEO = "Video";

    public static final String NEXT_PAGE = "NextPage";
    public static final String PREV_PAGE = "PrevPage";

    private static final String TAG = HttpRequestService.class.getSimpleName();

    private static final String HTTP_REQUEST_INFO = "HttpRequestInfo";
    private static final String AUTHORIZATION_CODE = "AuthorizationCode";
    private static final String EXTRAS_TYPE = "ExtrasType";

    private enum ExtrasType {
        HTTP_REQUEST_INFO, AUTHORIZATION_CODE
    }

    static final String USER_LIST_ACTION = "UserListAction";
    static final String VIDEO_LIST_ACTION = "VideoListAction";
    static final String GROUP_LIST_ACTION = "GroupListAction";
    static final String CHANNEL_LIST_ACTION = "ChannelListAction";
    static final String COMMENT_LIST_ACTION = "CommentListAction";
    static final String ACCESS_TOKEN_ACTION = "AccessTokenAction";
    static final String USER_ACTION = "UserAction";
    static final String CHANNEL_ACTION = "ChannelAction";
    static final String GROUP_ACTION = "GroupAction";
    static final String VIDEO_ACTION = "VideoAction";
    static final String IS_SUBSCRIBER_ACTION = "IsSubscriberAction";
    static final String CHANGE_SUBSCRIBE_STATE_ACTION = "ChangeSubscribeStateAction";
    static final String IS_MEMBER_ACTION = "IsMemberAction";
    static final String CHANGE_MEMBER_STATE_ACTION = "ChangeMemberStateAction";
    static final String IS_FOLLOWING_ACTION = "IsFollowingAction";
    static final String CHANGE_FOLLOW_STATE_ACTION = "ChangeFollowStateAction";
    static final String IS_LIKED_ACTION = "IsLikedAction";
    static final String CHANGE_LIKE_STATE_ACTION = "ChangeLikeStateAction";

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

    public static IntentFilter getUserIntentFilter() {
        IntentFilter userIntentFilter = new IntentFilter();

        userIntentFilter.addAction(USER_ACTION);

        return userIntentFilter;
    }

    public static IntentFilter getChannelIntentFilter() {
        IntentFilter channelIntentFilter = new IntentFilter();

        channelIntentFilter.addAction(CHANNEL_ACTION);

        return channelIntentFilter;
    }

    public static IntentFilter getGroupIntentFilter() {
        IntentFilter groupIntentFilter = new IntentFilter();

        groupIntentFilter.addAction(GROUP_ACTION);

        return groupIntentFilter;
    }

    public static IntentFilter getVideoIntentFilter() {
        IntentFilter videoIntentFilter = new IntentFilter();

        videoIntentFilter.addAction(VIDEO_ACTION);

        return videoIntentFilter;
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
        Log.d(TAG, "onCreate");
        super.onCreate();

        mExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        switch((ExtrasType) intent.getSerializableExtra(EXTRAS_TYPE)) {
            case AUTHORIZATION_CODE:
                Log.d(TAG, "onStartCommand: handle accessToken request");
                final String code = intent.getStringExtra(AUTHORIZATION_CODE);
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        RequestManager.getAccessToken(code, ACCESS_TOKEN_ACTION,
                                HttpRequestService.this);
                    }
                });
                break;
            case HTTP_REQUEST_INFO:
                Log.d(TAG, "onStartCommand: handle request");


                final HttpRequestInfo requestInfo = (HttpRequestInfo)
                        intent.getSerializableExtra(HTTP_REQUEST_INFO);

                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        RequestManager.handleRequest(requestInfo, HttpRequestService.this);
                    }
                });
                break;
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        mExecutor.shutdownNow();
    }
}
