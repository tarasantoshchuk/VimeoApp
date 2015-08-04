package com.example.tarasantoshchuk.vimeoapp.service;

import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.entity.channel.Channel;
import com.example.tarasantoshchuk.vimeoapp.entity.group.Group;
import com.example.tarasantoshchuk.vimeoapp.entity.user.User;
import com.example.tarasantoshchuk.vimeoapp.entity.video.Video;
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;
import com.example.tarasantoshchuk.vimeoapp.util.JSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;

public class RequestManager {
    private static final String TAG = RequestManager.class.getSimpleName();

    private static final String GRANT_TYPE_VALUE = "authorization_code";
    private static final String ACCESS_TOKEN_QUERY_FORMAT = "grant_type=%s&code=%s&redirect_uri=%s";

    private static final String POST_METHOD = "POST";

    private static final String CHARSET_UTF = "UTF-8";

    private static final String ACCEPT = "Accept";
    private static final String ACCEPT_VALUE = "application/vnd.vimeo.*+json;version=3.2";
    private static final String ACCEPT_CHARSET = "Accept-Charset";
    private static final String CHARSET = "charset";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_URL_ENCODED = "application/x-www-form-urlencoded";

    public static void getAccessToken(String code, String action, HttpRequestService service) {
        Log.d(TAG, "getAccessToken");

        try {
            String url = service.getString(R.string.access_token_address);
            String grantType = GRANT_TYPE_VALUE;
            String redirectUrl = service.getString(R.string.callback_url);

            String query = String.format(ACCESS_TOKEN_QUERY_FORMAT,
                    URLEncoder.encode(grantType, CHARSET_UTF),
                    URLEncoder.encode(code, CHARSET_UTF),
                    URLEncoder.encode(redirectUrl, CHARSET_UTF));

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(POST_METHOD);
            connection.setDoOutput(true);
            connection.setRequestProperty(ACCEPT, ACCEPT_VALUE);
            connection.setRequestProperty(ACCEPT_CHARSET, CHARSET);
            connection.setRequestProperty(AUTHORIZATION, AUTHORIZATION_PREFIX +
                    service.getString(R.string.client_info));

            connection.setRequestProperty(CONTENT_TYPE, CONTENT_URL_ENCODED);

            connection.getOutputStream().write(query.getBytes(CHARSET_UTF));

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());

            String accessToken = JSONParser.getAccessToken(json);
            User loggedUser = JSONParser.getUser(json);

            Intent resultIntent = new Intent();

            resultIntent.setAction(action);

            resultIntent.putExtra(HttpRequestService.ACCESS_TOKEN, accessToken);
            resultIntent.putExtra(HttpRequestService.USER, loggedUser);

            Log.d(TAG, "getAccessToken: success");
            service.sendBroadcast(resultIntent);

        } catch (Exception e) {
            e.printStackTrace();

            Intent failIntent = new Intent();

            failIntent.setAction(action);

            Log.d(TAG, "getAccessToken: fail");
            service.sendBroadcast(failIntent);
        }
    }

    public static void handleRequest(HttpRequestInfo requestInfo, String action,
                                     HttpRequestService service) {
    }
}
