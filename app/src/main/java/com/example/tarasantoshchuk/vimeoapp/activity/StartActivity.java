package com.example.tarasantoshchuk.vimeoapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserActivity;
import com.example.tarasantoshchuk.vimeoapp.util.Alerts;
import com.example.tarasantoshchuk.vimeoapp.util.AuthorizationInfo;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestInfo;

public class StartActivity extends Activity {
    private static final String TAG = StartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        /**
         * check if internet connection is available
         */
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();


        if(!isConnected) {

            Alerts.showConnectionFailedAlert(this);

        } else {

            AuthorizationInfo.init(this);

            if(!AuthorizationInfo.hasAccessToken()) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getAuthorizationAddress()));
                startActivity(intent);

            } else {

                Intent loggedUserActivity = new Intent(this, UserActivity.class);

                HttpRequestInfo userRequest = HttpRequestInfo.getMyInfoRequest();

                loggedUserActivity.setAction(UserActivity.LOGGED_USER_ACTION);
                loggedUserActivity.putExtras(UserActivity.getStartExtras(userRequest));

                startActivity(loggedUserActivity);
            }

        }
    }

    private String getAuthorizationAddress() {
        return new StringBuilder()
                .append(getString(R.string.authorization_address))
                .append("?response_type=code")
                .append("&client_id=")
                .append(getString(R.string.client_id))
                .append("&redirect_uri=")
                .append(getString(R.string.callback_url))
                .append("&scope=public private interact")
                .append("&state=")
                .append(getString(R.string.client_state))
                .toString();
    }
}
