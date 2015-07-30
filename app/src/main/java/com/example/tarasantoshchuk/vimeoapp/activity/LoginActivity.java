package com.example.tarasantoshchuk.vimeoapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.entity.user.User;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserActivity;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;
import com.example.tarasantoshchuk.vimeoapp.util.AuthorizationInfo;
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;
import com.example.tarasantoshchuk.vimeoapp.util.JSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends Activity {
    private static final String CODE = "code";
    private static final String STATE = "state";

    private AccessTokenReceiver mReceiver;
    private String mState;
    private String mCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri data = intent.getData();
        mState = data.getQueryParameter(STATE);
        mCode = data.getQueryParameter(CODE);

        mReceiver = new AccessTokenReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(mReceiver, HttpRequestService.getAccessTokenIntentFilter());

        if(mCode != null && mState.equals(getString(R.string.client_state))) {
            Intent getAccessTokenIntent = new Intent(this, HttpRequestService.class);

            getAccessTokenIntent.putExtras(HttpRequestService.getStartExtras(mCode));

            startService(getAccessTokenIntent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setCancelable(false)
                    .setTitle(getString(R.string.txt_auth_fail))
                    .setMessage(getString(R.string.txt_auth_fail_msg))
                    .setCancelable(false)
                    .setNeutralButton(getString(R.string.txt_close),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mReceiver);
    }

    private class AccessTokenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String accessToken = intent.getStringExtra(HttpRequestService.ACCESS_TOKEN);
            User loggedUser = intent.getParcelableExtra(HttpRequestService.USER);

            if(accessToken != null) {
                AuthorizationInfo.setAccessToken(accessToken);

                Intent loggedUserActivityIntent =
                        new Intent(LoginActivity.this, UserActivity.class);

                loggedUserActivityIntent.putExtras(UserActivity.getStartExtras(loggedUser));

                startActivity(loggedUserActivityIntent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setCancelable(false)
                        .setTitle(getString(R.string.txt_connection_fail))
                        .setMessage(getString(R.string.txt_check_connection))
                        .setNeutralButton(getString(R.string.txt_close),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                        .show();
            }
        }
    }
}
