package com.example.tarasantoshchuk.vimeoapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.entity.channel.Channel;
import com.example.tarasantoshchuk.vimeoapp.entity.channel.ChannelActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.group.Group;
import com.example.tarasantoshchuk.vimeoapp.entity.group.GroupActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.user.User;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.video.Video;
import com.example.tarasantoshchuk.vimeoapp.entity.video.VideoActivity;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;
import com.example.tarasantoshchuk.vimeoapp.util.Alerts;
import com.example.tarasantoshchuk.vimeoapp.util.AuthorizationInfo;
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;
import com.example.tarasantoshchuk.vimeoapp.util.JSONParser;

import java.util.Date;
import java.util.Random;

public class LoginActivity extends Activity {
    public static final String LOGGED_USER_ACTION = "LoggedUserAction";

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String CODE = "code";
    private static final String STATE = "state";

    private AccessTokenReceiver mReceiver;
    private String mState;
    private String mCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri data = intent.getData();
        mState = data.getQueryParameter(STATE);
        mCode = data.getQueryParameter(CODE);

        mReceiver = new AccessTokenReceiver();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

        registerReceiver(mReceiver, HttpRequestService.getAccessTokenIntentFilter());

        if(mCode != null && mState.equals(getString(R.string.client_state))) {
            Intent getAccessTokenIntent = new Intent(this, HttpRequestService.class);

            getAccessTokenIntent.putExtras(HttpRequestService.getStartExtras(mCode));

            startService(getAccessTokenIntent);
        } else {
            Alerts.showAuthorizationFailedAlert(this);
        }
    }

    @Override
    protected void onStop(){
        Log.d(TAG, "onStop");
        super.onStop();

        unregisterReceiver(mReceiver);
    }

    private class AccessTokenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "AccessTokenReceiver.onReceive");
            String accessToken = intent.getStringExtra(HttpRequestService.ACCESS_TOKEN);
            User loggedUser = intent.getParcelableExtra(HttpRequestService.USER);

            if(accessToken != null) {
                AuthorizationInfo.setAccessToken(accessToken);

                Intent loggedUserActivityIntent =
                        new Intent(LoginActivity.this, UserActivity.class);

                loggedUserActivityIntent.setAction(LOGGED_USER_ACTION);
                loggedUserActivityIntent.putExtras(UserActivity.getStartExtras(loggedUser));

                startActivity(loggedUserActivityIntent);
            } else {
                Alerts.showConnectionFailedAlert(LoginActivity.this);
            }
        }
    }
}
