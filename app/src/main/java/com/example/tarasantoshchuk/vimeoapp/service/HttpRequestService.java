package com.example.tarasantoshchuk.vimeoapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import com.example.tarasantoshchuk.vimeoapp.entity.channel.Channel;
import com.example.tarasantoshchuk.vimeoapp.entity.group.Group;
import com.example.tarasantoshchuk.vimeoapp.entity.user.User;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserList;
import com.example.tarasantoshchuk.vimeoapp.entity.video.Video;
import com.example.tarasantoshchuk.vimeoapp.entity.video.VideoList;
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class HttpRequestService extends Service {
    public static final String USER_LIST = "UserList";
    public static final String VIDEO_LIST = "VideoList";
    public static final String GROUP_LIST = "GroupList";
    public static final String CHANNEL_LIST = "ChannelList";

    public static final String NEXT_PAGE = "NextPage";
    public static final String PREV_PAGE = "PrevPage";

    private static final String HTTP_REQUEST_INFO = "HTTP_REQUEST_INFO";

    private static final String USER_LIST_ACTION = "UserListAction";
    private static final String VIDEO_LIST_ACTION = "VideoListAction";
    private static final String GROUP_LIST_ACTION = "GroupListAction";
    private static final String CHANNEL_LIST_ACTION = "ChannelListAction";

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
