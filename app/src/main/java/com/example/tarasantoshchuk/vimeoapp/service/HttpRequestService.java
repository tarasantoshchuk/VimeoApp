package com.example.tarasantoshchuk.vimeoapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import com.example.tarasantoshchuk.vimeoapp.entity.user.User;
import com.example.tarasantoshchuk.vimeoapp.entity.video.Video;
import com.example.tarasantoshchuk.vimeoapp.entity.video.VideoList;
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class HttpRequestService extends Service {
    public static final String USER_LIST = "UserList";
    public static final String VIDEO_LIST = "VideoList";

    public static final String NEXT_PAGE = "NextPage";
    public static final String PREV_PAGE = "PrevPage";

    private static final String HTTP_REQUEST_INFO = "HTTP_REQUEST_INFO";

    private static final String USER_LIST_ACTION = "UserListAction";
    private static final String VIDEO_LIST_ACTION = "VideoListAction";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

    public static Bundle getStartExtras(HttpRequestInfo info) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(HTTP_REQUEST_INFO, info);

        return bundle;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
