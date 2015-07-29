package com.example.tarasantoshchuk.vimeoapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.entity.channel.ChannelListActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.group.GroupListActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserListActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.video.VideoListActivity;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;
import com.example.tarasantoshchuk.vimeoapp.util.AuthorizationInfo;
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

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
                try {
                    /**
                     * TODO: fix not working code
                     */
//                    String url = " https://api.vimeo.com/oauth/access_token";
//                    String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
//                    String param1 = "authorization_code";
//                    String param2 = code;
//                    String param3 = getString(R.string.callback_url);
//
//                    String query = String.format("grant_type=%s&code=%s&redirect_uri=%s",
//                            URLEncoder.encode(param1, charset),
//                            URLEncoder.encode(param2, charset),
//                            URLEncoder.encode(param3, charset));
//
//                    //url += "?" + query;
//
//                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//                    //connection.setRequestMethod("POST");
//                    connection.setDoOutput(true); // Triggers POST.
//                    connection.setRequestProperty("Accept", "application/vnd.vimeo.*+json;version=3.2");
//                    connection.setRequestProperty("Accept-Charset", charset);
//                    connection.setRequestProperty("Authorization", "basic " +
//                            (getString(R.string.client_id) + ':' + getString(R.string.client_secret)));
//
//                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//                    connection.getOutputStream().write(query.getBytes(charset));
//
//                    int responseCode = connection.getResponseCode();

                    return "access_token";
                } catch (Exception e) {
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if(s.equals(getString(R.string.txt_empty))) {

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
        };

        getAccessTokenTask.execute();
    }
}
