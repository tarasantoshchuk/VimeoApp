package com.example.tarasantoshchuk.vimeoapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;

public class HttpRequestService extends Service {
    public static final String USER_LIST = "UserList";

    private static final String HTTP_REQUEST_INFO = "HTTP_REQUEST_INFO";

    private static final String USER_LIST_ACTION = "UserListAction";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static IntentFilter getUserListIntentFilter() {
        IntentFilter userListFilter = new IntentFilter();

        userListFilter.addAction(USER_LIST_ACTION);

        return userListFilter;
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
