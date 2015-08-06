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
    /**
     * keys for intent extras in broadcasts
     */
    public static final String KEY_USER_LIST_RESULT = "UserList";
    public static final String KEY_VIDEO_LIST_RESULT = "VideoList";
    public static final String KEY_GROUP_LIST_RESULT = "GroupList";
    public static final String KEY_CHANNEL_LIST_RESULT = "ChannelList";
    public static final String KEY_COMMENT_LIST_RESULT = "CommentList";
    public static final String KEY_ACCESS_TOKEN_RESULT = "AccessToken";
    public static final String KEY_USER_RESULT = "User";
    public static final String KEY_CHANNEL_RESULT = "Channel";
    public static final String KEY_GROUP_RESULT = "Group";
    public static final String KEY_VIDEO_RESULT = "Video";
    public static final String KEY_BOOLEAN_RESULT = "Boolean";

    public static final String KEY_NEXT_PAGE_RESULT = "NextPage";
    public static final String KEY_PREV_PAGE_RESULT = "PrevPage";

    private static final String TAG = HttpRequestService.class.getSimpleName();

    private static final String HTTP_REQUEST_INFO = "HttpRequestInfo";
    private static final String AUTHORIZATION_CODE = "AuthorizationCode";
    private static final String EXTRAS_TYPE = "ExtrasType";

    private enum ExtrasType {
        HTTP_REQUEST_INFO, AUTHORIZATION_CODE
    }

    static final String ACTION_USER_LIST = "UserListAction";
    static final String ACTION_VIDEO_LIST = "VideoListAction";
    static final String ACTION_GROUP_LIST = "GroupListAction";
    static final String ACTION_CHANNEL_LIST = "ChannelListAction";
    static final String ACTION_COMMENT_LIST = "CommentListAction";
    static final String ACTION_ACCESS_TOKEN = "AccessTokenAction";
    static final String ACTION_USER = "UserAction";
    static final String ACTION_CHANNEL = "ChannelAction";
    static final String ACTION_GROUP = "GroupAction";
    static final String ACTION_VIDEO = "VideoAction";
    static final String ACTION_IS_SUBSCRIBER = "IsSubscriberAction";
    static final String ACTION_CHANGE_SUBSCRIBE_STATE = "ChangeSubscribeStateAction";
    static final String ACTION_IS_MEMBER = "IsMemberAction";
    static final String ACTION_CHANGE_MEMBER_STATE = "ChangeMemberStateAction";
    static final String ACTION_IS_FOLLOWING = "IsFollowingAction";
    static final String ACTION_CHANGE_FOLLOW_STATE = "ChangeFollowStateAction";
    static final String ACTION_IS_LIKED = "IsLikedAction";
    static final String ACTION_CHANGE_LIKE_STATE = "ChangeLikeStateAction";

    public static IntentFilter getUserListIntentFilter() {
        IntentFilter userListFilter = new IntentFilter();

        userListFilter.addAction(ACTION_USER_LIST);

        return userListFilter;
    }

    public static IntentFilter getVideoListIntentFilter() {
        IntentFilter videoListFilter = new IntentFilter();

        videoListFilter.addAction(ACTION_VIDEO_LIST);

        return videoListFilter;
    }

    public static IntentFilter getGroupListIntentFilter() {
        IntentFilter groupListFilter = new IntentFilter();

        groupListFilter.addAction(ACTION_GROUP_LIST);

        return groupListFilter;
    }

    public static IntentFilter getChannelListIntentFilter() {
        IntentFilter channelListFilter = new IntentFilter();

        channelListFilter.addAction(ACTION_CHANNEL_LIST);

        return channelListFilter;
    }

    public static IntentFilter getCommentListIntentFilter() {
        IntentFilter commentListFilter = new IntentFilter();

        commentListFilter.addAction(ACTION_COMMENT_LIST);

        return commentListFilter;
    }

    public static IntentFilter getAccessTokenIntentFilter() {
        IntentFilter accessTokenFilter = new IntentFilter();

        accessTokenFilter.addAction(ACTION_ACCESS_TOKEN);

        return accessTokenFilter;
    }

    public static IntentFilter getUserIntentFilter() {
        IntentFilter userIntentFilter = new IntentFilter();

        userIntentFilter.addAction(ACTION_USER);

        return userIntentFilter;
    }

    public static IntentFilter getChannelIntentFilter() {
        IntentFilter channelIntentFilter = new IntentFilter();

        channelIntentFilter.addAction(ACTION_CHANNEL);

        return channelIntentFilter;
    }

    public static IntentFilter getGroupIntentFilter() {
        IntentFilter groupIntentFilter = new IntentFilter();

        groupIntentFilter.addAction(ACTION_GROUP);

        return groupIntentFilter;
    }

    public static IntentFilter getVideoIntentFilter() {
        IntentFilter videoIntentFilter = new IntentFilter();

        videoIntentFilter.addAction(ACTION_VIDEO);

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

        /**
         * if service is not restarted by OS
         */
        if(intent != null) {
            switch ((ExtrasType) intent.getSerializableExtra(EXTRAS_TYPE)) {
                case AUTHORIZATION_CODE:
                    Log.d(TAG, "onStartCommand: handle accessToken request");
                    final String code = intent.getStringExtra(AUTHORIZATION_CODE);
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            RequestManager.getAccessToken(code, ACTION_ACCESS_TOKEN,
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
