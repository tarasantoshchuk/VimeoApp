package com.example.tarasantoshchuk.vimeoapp.service;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.entity.channel.Channel;
import com.example.tarasantoshchuk.vimeoapp.entity.comment.Comment;
import com.example.tarasantoshchuk.vimeoapp.entity.group.Group;
import com.example.tarasantoshchuk.vimeoapp.entity.user.User;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserList;
import com.example.tarasantoshchuk.vimeoapp.entity.video.Video;
import com.example.tarasantoshchuk.vimeoapp.entity.video.VideoList;
import com.example.tarasantoshchuk.vimeoapp.util.AuthorizationInfo;
import com.example.tarasantoshchuk.vimeoapp.util.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
    private static final String AUTHORIZATION_BASIC = "Basic ";
    private static final String AUTHORIZATION_BEARER = "bearer ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_URL_ENCODED = "application/x-www-form-urlencoded";

    private static final Integer SUCCESSFUL_STATUS_CODE_MIN = 200;
    private static final Integer SUCCESSFUL_STATUS_CODE_MAX = 300;

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
            connection.setRequestProperty(AUTHORIZATION, AUTHORIZATION_BASIC +
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
            User loggedUser = JSONParser.getUserFromAccessTokenRequest(json);

            Intent resultIntent = new Intent();

            resultIntent.setAction(action);

            resultIntent.putExtra(HttpRequestService.KEY_ACCESS_TOKEN_RESULT, accessToken);
            resultIntent.putExtra(HttpRequestService.KEY_USER_RESULT, loggedUser);

            Log.d(TAG, "getAccessToken: success");
            service.sendBroadcast(resultIntent);

        } catch (Exception e) {
            Log.e(TAG, "getAccessToken: exception", e);

            handleError(action, service);
        }
    }

    private static void handleError(String action, HttpRequestService service) {
        Intent failIntent = new Intent();

        failIntent.setAction(action);

        service.sendBroadcast(failIntent);
    }

    public static void handleRequest(HttpRequestInfo requestInfo, HttpRequestService service) {
        Log.d(TAG, "handleRequest");

        String urlString = HttpRequestInfo.BASIC_URL + requestInfo.mEndpoint;

        if(requestInfo.mMethod == HttpRequestInfo.RequestMethod.GET) {
            urlString = addQueryParameters(urlString, requestInfo.mParameters);
        } else {
            // non-GET requests provide parameters via request body
        }

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(TAG, "handleRequest: exception", e);
            throw new RuntimeException(e);
        }

        HttpURLConnection connection = null;
        Integer responseCode = null;
        JSONObject responseJSON = null;

        try {
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(requestInfo.mMethod.toString());

            setConnectionHeaders(connection);

            if(requestInfo.mMethod != HttpRequestInfo.RequestMethod.GET) {
                addBody(connection, requestInfo.mParameters);
            }

            responseCode = connection.getResponseCode();

            responseJSON = getJsonResponse(connection);
        } catch (ProtocolException e) {
            Log.e(TAG, "handleRequest: exception", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.e(TAG, "handleRequest: exception", e);

            handleError(requestInfo.mAction, service);
        }

        Bundle broadcastExtras = new Bundle();

        switch(requestInfo.mResult) {
            case USER:
                putUserResultExtras(responseCode, responseJSON, broadcastExtras);
                break;
            case USER_LIST:
                putUserListResultExtras(responseCode, responseJSON, requestInfo, broadcastExtras);
                break;
            case VIDEO:
                putVideoResultExtras(responseCode, responseJSON, broadcastExtras);
                break;
            case VIDEO_LIST:
                putVideoListResultExtras(responseCode, responseJSON, requestInfo, broadcastExtras);
                break;
            case GROUP:
                putGroupResultExtras(responseCode, responseJSON, broadcastExtras);
                break;
            case GROUP_LIST:
                putGroupListResultExtras(responseCode, responseJSON, requestInfo, broadcastExtras);
                break;
            case CHANNEL:
                putChannelResultExtras(responseCode, responseJSON, broadcastExtras);
                break;
            case CHANNEL_LIST:
                putChannelListResultExtras(responseCode, responseJSON, requestInfo,
                        broadcastExtras);
                break;
            case COMMENT_LIST:
                putCommentListResultExtras(responseCode, responseJSON, requestInfo,
                        broadcastExtras);
                break;
            case BOOLEAN:
                putBooleanResultExtras(responseCode, responseJSON, broadcastExtras);
                break;
        }

        Intent resultIntent = new Intent();

        resultIntent.setAction(requestInfo.mAction);
        resultIntent.putExtras(broadcastExtras);

        service.sendBroadcast(resultIntent);
    }

    private static void putBooleanResultExtras(Integer responseCode, JSONObject responseJSON,
                                               Bundle outBundle) {
        Boolean result = isSuccessfulRequest(responseCode);

        outBundle.putBoolean(HttpRequestService.KEY_BOOLEAN_RESULT, result);
    }

    private static Boolean isSuccessfulRequest(Integer responseCode) {
        return responseCode >= SUCCESSFUL_STATUS_CODE_MIN &&
                responseCode < SUCCESSFUL_STATUS_CODE_MAX;
    }

    private static void putCommentListResultExtras(Integer responseCode, JSONObject responseJSON,
                                                   HttpRequestInfo requestInfo, Bundle outBundle) {
        if(isSuccessfulRequest(responseCode)) {
            ArrayList<Comment> commentList = JSONParser.getCommentList(responseJSON);

            outBundle.putParcelableArrayList(HttpRequestService.KEY_COMMENT_LIST_RESULT,
                    commentList);

            putAdjacentPagesRequests(responseJSON, requestInfo, outBundle);
        }
    }

    private static void putChannelListResultExtras(Integer responseCode, JSONObject responseJSON,
                                                   HttpRequestInfo requestInfo, Bundle outBundle) {
        if (isSuccessfulRequest(responseCode)) {
            ArrayList<Channel> channelList = JSONParser.getChannelList(responseJSON);

            outBundle.putParcelableArrayList(HttpRequestService.KEY_CHANNEL_LIST_RESULT,
                    channelList);

            putAdjacentPagesRequests(responseJSON, requestInfo,  outBundle);
        }
    }

    private static void putChannelResultExtras(Integer responseCode, JSONObject responseJSON,
                                               Bundle outBundle) {
        if(isSuccessfulRequest(responseCode)) {
            Channel channel = JSONParser.getChannel(responseJSON);

            outBundle.putParcelable(HttpRequestService.KEY_CHANNEL_RESULT, channel);
        }
    }

    private static void putGroupListResultExtras(Integer responseCode, JSONObject responseJSON,
                                                 HttpRequestInfo requestInfo, Bundle outBundle) {
        if(isSuccessfulRequest(responseCode)) {
            ArrayList<Group> groupList = JSONParser.getGroupList(responseJSON);

            outBundle.putParcelableArrayList(HttpRequestService.KEY_GROUP_LIST_RESULT, groupList);

            putAdjacentPagesRequests(responseJSON, requestInfo, outBundle);
        }
    }

    private static void putGroupResultExtras(Integer responseCode, JSONObject responseJSON,
                                             Bundle outBundle) {
        if(isSuccessfulRequest(responseCode)) {
            Group group = JSONParser.getGroup(responseJSON);

            outBundle.putParcelable(HttpRequestService.KEY_GROUP_RESULT, group);
        }
    }

    private static void putVideoListResultExtras(Integer responseCode, JSONObject responseJSON,
                                                 HttpRequestInfo requestInfo, Bundle outBundle) {
        if(isSuccessfulRequest(responseCode)) {
            VideoList videoList = JSONParser.getVideoList(responseJSON);

            outBundle.putParcelable(HttpRequestService.KEY_VIDEO_LIST_RESULT, videoList);

            putAdjacentPagesRequests(responseJSON, requestInfo,  outBundle);
        }
    }

    private static void putVideoResultExtras(Integer responseCode, JSONObject responseJSON,
                                             Bundle outBundle) {
        if(isSuccessfulRequest(responseCode)) {
            Video video = JSONParser.getVideo(responseJSON);

            outBundle.putParcelable(HttpRequestService.KEY_VIDEO_RESULT, video);
        }
    }

    private static void putUserListResultExtras(Integer responseCode, JSONObject responseJSON,
                                                HttpRequestInfo requestInfo, Bundle outBundle) {
        if(isSuccessfulRequest(responseCode)) {
            UserList userList = JSONParser.getUserList(responseJSON);

            outBundle.putParcelable(HttpRequestService.KEY_USER_LIST_RESULT, userList);

            putAdjacentPagesRequests(responseJSON, requestInfo, outBundle);
        }
    }

    private static void putAdjacentPagesRequests(JSONObject responseJSON,
                                                 HttpRequestInfo requestInfo, Bundle outBundle) {
        String nextPage = JSONParser.getNextPageEndpoint(responseJSON);
        if(nextPage != null) {
            outBundle.putSerializable(HttpRequestService.KEY_NEXT_PAGE_RESULT,
                    HttpRequestInfo.updateEndpoint(nextPage, requestInfo));
        }

        String prevPage = JSONParser.getPrevPageEndpoint(responseJSON);
        if(prevPage != null) {
            outBundle.putSerializable(HttpRequestService.KEY_PREV_PAGE_RESULT,
                    HttpRequestInfo.updateEndpoint(prevPage, requestInfo));
        }
    }

    private static void putUserResultExtras(Integer responseCode, JSONObject responseJSON,
                                            Bundle outBundle) {
        if(isSuccessfulRequest(responseCode)) {
            User user = JSONParser.getUser(responseJSON);

            outBundle.putParcelable(HttpRequestService.KEY_USER_RESULT, user);
        }
    }

    private static void setConnectionHeaders(HttpURLConnection connection) {
        Log.d(TAG, "setConnectionHeaders");

        connection.setRequestProperty(ACCEPT, ACCEPT_VALUE);
        connection.setRequestProperty(ACCEPT_CHARSET, CHARSET);
        connection.setRequestProperty(AUTHORIZATION, AUTHORIZATION_BEARER +
                AuthorizationInfo.getAccessToken());

        connection.setRequestProperty(CONTENT_TYPE, CONTENT_URL_ENCODED);
    }

    private static String addQueryParameters(String url, HashMap<String, String> params) {
        Log.d(TAG, "addQueryParameters\nurl: " + url + "\nparams: " + params);

        StringBuilder queryBuilder = new StringBuilder();

        for(String key: params.keySet()) {
            if(queryBuilder.length() != 0) {
                queryBuilder.append("&");
            } else {
                queryBuilder.append("?");
            }
            try {
                queryBuilder
                        .append(key)
                        .append("=")
                        .append(URLEncoder.encode(params.get(key), CHARSET_UTF));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "addQueryParameters: exception", e);
                throw new RuntimeException(e);
            }
        }

        return url + queryBuilder.toString();
    }

    private static void addBody(HttpURLConnection connection, LinkedHashMap<String, String> params)
            throws IOException {

        Log.d(TAG, "addBody");

        connection.setDoOutput(true);
        StringBuilder queryBuilder = new StringBuilder();

        for(String key: params.keySet()) {
            if(queryBuilder.length() != 0) {
                queryBuilder.append("&");
            }
            try {
                queryBuilder
                        .append(key)
                        .append("=")
                        .append(URLEncoder.encode(params.get(key), CHARSET_UTF));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "addBody: exception", e);
                throw new RuntimeException(e);
            }
        }

        String query = queryBuilder.toString();

        try {
            connection.getOutputStream().write(query.getBytes(CHARSET_UTF));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "addBody: exception", e);
            throw new RuntimeException(e);
        }
    }

    private static JSONObject getJsonResponse(HttpURLConnection connection) throws IOException {
        Log.d(TAG, "getJsonResponse");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        String inputLine;
        StringBuilder response = new StringBuilder();

        do {
            inputLine = in.readLine();

            if(inputLine == null) {
                break;
            }

            response.append(inputLine);

        } while(true);

        in.close();

        try {
            return new JSONObject(response.toString());
        } catch (JSONException e) {
            Log.e(TAG, "getJsonResponse: exception", e);
            throw new RuntimeException(e);
        }
    }
}
