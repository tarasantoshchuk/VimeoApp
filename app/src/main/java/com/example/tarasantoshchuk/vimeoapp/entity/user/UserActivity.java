package com.example.tarasantoshchuk.vimeoapp.entity.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.activity.LoginActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.channel.ChannelListActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.group.GroupListActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.video.VideoListActivity;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;
import com.example.tarasantoshchuk.vimeoapp.util.Alerts;
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;

import java.io.IOException;
import java.net.URL;

public class UserActivity extends Activity {
    private static final String TAG = UserActivity.class.getSimpleName();

    private static final String USER = "User";
    private static final String USER_REQUEST = "UserRequest";
    private static final String STARTUP_EXTRA = "StartupExtra";

    private static final String VIDEO_LIST_TITLE = "User videos";
    private static final String LIKED_VIDEOS_TITLE = "Liked videos";
    private static final String FOLLOWED_USERS_TITLE = "Followed";
    private static final String FOLLOWERS_TITLE = "Followers";
    private static final String GROUP_LIST_TITLE = "User's groups";
    private static final String CHANNEL_LIST_TITLE = "User's channels";

    private enum StartupExtra {
        USER, USER_REQUEST
    }

    public static Bundle getStartExtras(User user) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(STARTUP_EXTRA, StartupExtra.USER);

        bundle.putParcelable(USER, user);

        return bundle;
    }

    public static Bundle getStartExtras(HttpRequestInfo userRequestInfo) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(STARTUP_EXTRA, StartupExtra.USER_REQUEST);

        bundle.putSerializable(USER_REQUEST, userRequestInfo);

        return bundle;
    }

    private User mUser;

    private ImageView mImgUserPicture;

    private TextView mTxtUserName;
    private TextView mTxtUserLocation;
    private TextView mTxtUserJoined;
    private TextView mTxtUserBio;

    private TextView mTxtVideosTitle;
    private TextView mTxtLikesTitle;
    private TextView mTxtFllwngTitle;
    private TextView mTxtFllwrsTitle;
    private TextView mTxtGroupsTitle;
    private TextView mTxtChannelsTitle;

    private TextView mTxtVideosCount;
    private TextView mTxtLikesCount;
    private TextView mTxtFllwngCount;
    private TextView mTxtFllwrsCount;
    private TextView mTxtGroupsCount;
    private TextView mTxtChannelsCount;

    private UserReceiver mReceiver;

    private HttpRequestInfo mRequestInfo;

    private AsyncTask<Void, Void, Bitmap> mLoadPictureTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mReceiver = new UserReceiver();

        setViews();

        StartupExtra startupType = (StartupExtra)
                getIntent().getSerializableExtra(STARTUP_EXTRA);

        if(startupType == StartupExtra.USER) {
            mUser = getIntent().getParcelableExtra(USER);

            setTexts();
            setOnClickListeners();
            loadPicture();
        } else {
            mRequestInfo = (HttpRequestInfo)
                    getIntent().getSerializableExtra(USER_REQUEST);
        }
    }

    private void startHttpService(HttpRequestInfo requestInfo) {
        Intent userRequestIntent = new Intent(this, HttpRequestService.class);

        userRequestIntent.putExtras(HttpRequestService.getStartExtras(requestInfo));

        startService(userRequestIntent);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

        registerReceiver(mReceiver, HttpRequestService.getUserIntentFilter());

        startHttpService(mRequestInfo);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

        if(mLoadPictureTask != null && mLoadPictureTask.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadPictureTask.cancel(false);
        }

        unregisterReceiver(mReceiver);
    }

    private void setViews() {
        mImgUserPicture = (ImageView) findViewById(R.id.imgUserPicture);

        mTxtUserName = (TextView) findViewById(R.id.txtUserName);
        mTxtUserLocation = (TextView) findViewById(R.id.txtUserLocation);
        mTxtUserJoined = (TextView) findViewById(R.id.txtUserJoined);
        mTxtUserBio = (TextView) findViewById(R.id.txtUserBio);

        mTxtVideosTitle = (TextView) findViewById(R.id.txtVideosTitle);
        mTxtLikesTitle = (TextView) findViewById(R.id.txtLikesTitle);
        mTxtFllwngTitle = (TextView) findViewById(R.id.txtFllwngTitle);
        mTxtFllwrsTitle = (TextView) findViewById(R.id.txtFllwrsTitle);
        mTxtGroupsTitle = (TextView) findViewById(R.id.txtGroupsTitle);
        mTxtChannelsTitle = (TextView) findViewById(R.id.txtChannelsTitle);

        mTxtVideosCount = (TextView) findViewById(R.id.txtVideosCount);
        mTxtLikesCount = (TextView) findViewById(R.id.txtLikesCount);
        mTxtFllwngCount = (TextView) findViewById(R.id.txtFllwngCount);
        mTxtFllwrsCount = (TextView) findViewById(R.id.txtFllwrsCount);
        mTxtGroupsCount = (TextView) findViewById(R.id.txtGroupsCount);
        mTxtChannelsCount = (TextView) findViewById(R.id.txtChannelsCount);
    }

    private void setTexts() {
        mTxtUserName.setText(mUser.getName());
        mTxtUserLocation.setText(mUser.getLocation());
        mTxtUserJoined.setText(mUser.getJoinedString());
        mTxtUserBio.setText(mUser.getBio());

        mTxtVideosCount.setText(Integer.toString(mUser.getVideoCount()));
        mTxtLikesCount.setText(Integer.toString(mUser.getLikesCount()));
        mTxtFllwngCount.setText(Integer.toString(mUser.getFollowingCount()));
        mTxtFllwrsCount.setText(Integer.toString(mUser.getFollowersCount()));
        mTxtGroupsCount.setText(Integer.toString(mUser.getGroupsCount()));
        mTxtChannelsCount.setText(Integer.toString(mUser.getChannelsCount()));
    }

    private void setOnClickListeners() {
        mTxtVideosTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userVideoListIntent = new Intent(UserActivity.this, VideoListActivity.class);

                HttpRequestInfo userVideosRequest =
                        HttpRequestInfo.getUserVideosRequest(mUser.getId());

                userVideoListIntent.putExtras(VideoListActivity
                        .getStartExtras(VIDEO_LIST_TITLE, userVideosRequest));

                Log.d(TAG, "start VideoListActivity of user videos");
                startActivity(userVideoListIntent);
            }
        });

        mTxtLikesTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent likedVideosIntent = new Intent(UserActivity.this, VideoListActivity.class);

                HttpRequestInfo likedVideosRequest =
                        HttpRequestInfo.getUserLikes(mUser.getId());

                likedVideosIntent.putExtras(VideoListActivity
                        .getStartExtras(LIKED_VIDEOS_TITLE, likedVideosRequest));

                Log.d(TAG, "start VideoListActivity of videos liked by user");
                startActivity(likedVideosIntent);
            }
        });

        mTxtFllwngTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fllwngUsersIntent = new Intent(UserActivity.this, UserListActivity.class);

                HttpRequestInfo fllwngUsersRequest =
                        HttpRequestInfo.getFollowedUsersRequest(mUser.getId());

                fllwngUsersIntent.putExtras(UserListActivity
                        .getStartExtras(FOLLOWED_USERS_TITLE, fllwngUsersRequest));

                Log.d(TAG, "start UserListActivity of followed users");
                startActivity(fllwngUsersIntent);
            }
        });

        mTxtFllwrsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fllwrsIntent = new Intent(UserActivity.this, UserListActivity.class);

                HttpRequestInfo fllwrsRequest =
                        HttpRequestInfo.getFollowedUsersRequest(mUser.getId());

                fllwrsIntent.putExtras(UserListActivity
                        .getStartExtras(FOLLOWERS_TITLE, fllwrsRequest));

                Log.d(TAG, "start UserListActivity of user followers");
                startActivity(fllwrsIntent);
            }
        });
        mTxtGroupsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupsIntent = new Intent(UserActivity.this, GroupListActivity.class);

                HttpRequestInfo groupsRequest = HttpRequestInfo.getUserGroupsRequest(mUser.getId());

                groupsIntent.putExtras(GroupListActivity
                        .getStartExtras(GROUP_LIST_TITLE, groupsRequest));

                Log.d(TAG, "start GroupListActivity of user's groups");
                startActivity(groupsIntent);
            }
        });

        mTxtChannelsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent channelsIntent = new Intent(UserActivity.this, ChannelListActivity.class);

                HttpRequestInfo channelsRequest =
                        HttpRequestInfo.getUserChannelsRequest(mUser.getId());

                channelsIntent.putExtras(ChannelListActivity
                        .getStartExtras(CHANNEL_LIST_TITLE, channelsRequest));

                Log.d(TAG, "start ChannelListActivity of user's channels");
                startActivity(channelsIntent);
            }
        });
    }

    private void loadPicture() {
        if(mUser.isPictureLoaded()) {
            mImgUserPicture.setImageBitmap(mUser.getPicture());
        } else {

            mLoadPictureTask = (new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {

                    try {

                        URL url = new URL(mUser.getPictureUrl());
                        return BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    Log.d(TAG, "LoadPictureTask.onPostExecute");
                    if (bitmap == null) {

                        Toast.makeText(UserActivity.this, getString(R.string.txt_bitmap_load_fail),
                                Toast.LENGTH_LONG).show();

                    } else {
                        mImgUserPicture.setImageBitmap(bitmap);
                        mUser.setPicture(bitmap);
                    }
                }
            }).execute();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK &&
                getIntent().getAction().equals(LoginActivity.LOGGED_USER_ACTION)) {
            Log.d(TAG, "back key press intercepted");
            return true;
        } else {
            return false;
        }
    }

    private class UserReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "UserReceiver.onReceive");
            User user = intent.getParcelableExtra(HttpRequestService.USER);

            if(user != null) {
                mUser = user;
                setTexts();
                setOnClickListeners();
                loadPicture();
            } else {
                Alerts.showConnectionFailedAlert(UserActivity.this);
            }
        }
    }
}
