package com.example.tarasantoshchuk.vimeoapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.tarasantoshchuk.vimeoapp.util.AuthorizationInfo;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        final String code = data.getQueryParameter("code");


        AsyncTask<Void, Void, String> getAccessTokenTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... args) {
                /**
                 * exchange code token for access_token and return it
                 */
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                AuthorizationInfo.setAccessToken(s);
                /**
                 * launch logged user profile/videos view
                 */
            }
        };

        getAccessTokenTask.execute();
    }
}
