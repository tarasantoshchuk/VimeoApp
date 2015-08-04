package com.example.tarasantoshchuk.vimeoapp.entity.group;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserListActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.video.VideoListActivity;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;
import com.example.tarasantoshchuk.vimeoapp.util.Alerts;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestInfo;

import java.io.IOException;
import java.net.URL;

public class GroupActivity extends Activity {
    private static final String TAG = GroupActivity.class.getSimpleName();

    private static final String GROUP = "Group";
    private static final String GROUP_REQUEST = "GroupRequest";
    private static final String STARTUP_EXTRA = "StartupExtra";

    private static final String MEMBER_LIST_TITLE = "Members";
    private static final String VIDEOS_LIST_TITLE = "VideosTitle";

    public GroupActivity() {
    }

    private enum StartupExtra {
        GROUP, GROUP_REQUEST
    }

    public static Bundle getStartExtras(Group group) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(STARTUP_EXTRA, StartupExtra.GROUP);
        bundle.putParcelable(GROUP, group);

        return bundle;
    }

    public static Bundle getStartExtras(HttpRequestInfo groupRequestInfo) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(STARTUP_EXTRA, StartupExtra.GROUP_REQUEST);
        bundle.putSerializable(GROUP_REQUEST, groupRequestInfo);

        return bundle;
    }

    private Group mGroup;

    private ImageView mImgGroupPicture;

    private TextView mTxtGroupName;
    private TextView mTxtGroupCreated;
    private TextView mTxtGroupDescription;

    private TextView mTxtGroupOwnerTitle;
    private TextView mTxtGroupUsersTitle;
    private TextView mTxtGroupVideosTitle;

    private TextView mTxtGroupOwnerName;
    private TextView mTxtGroupUsersCount;
    private TextView mTxtGroupVideosCount;

    private GroupReceiver mReceiver;

    private HttpRequestInfo mRequestInfo;

    private AsyncTask<Void, Void, Bitmap> mLoadPictureTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mReceiver = new GroupReceiver();

        setViews();

        StartupExtra startupType = (StartupExtra)
                getIntent().getSerializableExtra(STARTUP_EXTRA);

        if(startupType == StartupExtra.GROUP) {
            mGroup = getIntent().getParcelableExtra(GROUP);

            setTexts();
            setOnClickListeners();
            loadPicture();
        } else {
            mRequestInfo = (HttpRequestInfo)
                    getIntent().getSerializableExtra(GROUP_REQUEST);
        }
    }

    private void startHttpService (HttpRequestInfo requestInfo) {
        Log.d(TAG, "startHttpService");

        Intent groupRequestIntent = new Intent(this, HttpRequestService.class);

        groupRequestIntent.putExtras(HttpRequestService.getStartExtras(requestInfo));

        startService(groupRequestIntent);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

        registerReceiver(mReceiver, HttpRequestService.getGroupIntentFilter());

        if(mGroup == null) {
            startHttpService(mRequestInfo);
        }
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
        mImgGroupPicture = (ImageView) findViewById(R.id.imgGroupPicture);

        mTxtGroupName = (TextView) findViewById(R.id.txtGroupName);
        mTxtGroupCreated = (TextView) findViewById(R.id.txtGroupCreated);
        mTxtGroupDescription = (TextView) findViewById(R.id.txtGroupDescription);

        mTxtGroupOwnerTitle = (TextView) findViewById(R.id.txtGroupOwnerTitle);
        mTxtGroupUsersTitle = (TextView) findViewById(R.id.txtGroupUsersTitle);
        mTxtGroupVideosTitle = (TextView) findViewById(R.id.txtGroupVideosTitle);

        mTxtGroupOwnerName = (TextView) findViewById(R.id.txtGroupOwnerName);
        mTxtGroupUsersCount = (TextView) findViewById(R.id.txtGroupUserCount);
        mTxtGroupVideosCount = (TextView) findViewById(R.id.txtGroupVideosCount);
    }

    private void setTexts() {
        mTxtGroupName.setText(mGroup.getName());
        mTxtGroupCreated.setText(mGroup.getCreatedString());
        mTxtGroupDescription.setText(mGroup.getDescription());

        mTxtGroupOwnerName.setText(mGroup.getOwner().getName());
        mTxtGroupUsersCount.setText(Integer.toString(mGroup.getUsersCount()));
        mTxtGroupVideosCount.setText(Integer.toString(mGroup.getVideosCount()));
    }

    private void setOnClickListeners() {
        mTxtGroupOwnerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ownerIntent = new Intent(GroupActivity.this, UserActivity.class);

                HttpRequestInfo ownerRequest = HttpRequestInfo
                        .getUserInfoRequest(mGroup.getOwner().getId());

                ownerIntent.putExtras(UserActivity.getStartExtras(ownerRequest));

                Log.d(TAG, "start UserActivity: group's owner");
                startActivity(ownerIntent);
            }
        });

        mTxtGroupUsersTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGroup.getUsersCount() != 0) {
                    Intent membersIntent =
                            new Intent(GroupActivity.this, UserListActivity.class);

                    HttpRequestInfo membersRequest =
                            HttpRequestInfo.getGroupUsersRequest(mGroup.getId());

                    membersIntent.putExtras(UserListActivity
                            .getStartExtras(MEMBER_LIST_TITLE, membersRequest));

                    Log.d(TAG, "start UserListActivity: group members");
                    startActivity(membersIntent);
                } else {
                    Toast.makeText(GroupActivity.this, getString(R.string.txt_no_members),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTxtGroupVideosTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGroup.getVideosCount() != 0) {
                    Intent videosIntent = new Intent(GroupActivity.this, VideoListActivity.class);

                    HttpRequestInfo videosRequest =
                            HttpRequestInfo.getGroupVideosRequest(mGroup.getId());

                    videosIntent.putExtras(VideoListActivity
                            .getStartExtras(VIDEOS_LIST_TITLE, videosRequest));

                    Log.d(TAG, "start VideoListActivity: group videos");
                    startActivity(videosIntent);
                } else {
                    Toast.makeText(GroupActivity.this, getString(R.string.txt_no_videos),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadPicture() {
        if (mGroup.isPictureLoaded()) {
            mImgGroupPicture.setImageBitmap(mGroup.getPicture());
        } else {

            mLoadPictureTask = (new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {

                    try {

                        URL url = new URL(mGroup.getPictureUrl());
                        return BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    Log.d(TAG, "LoadPictureTask.onPostExecute");
                    if(bitmap == null) {

                        Toast.makeText(GroupActivity.this, getString(R.string.txt_bitmap_load_fail),
                                Toast.LENGTH_LONG).show();

                    } else {
                        mImgGroupPicture.setImageBitmap(bitmap);
                        mGroup.setPicture(bitmap);
                    }
                }
            }).execute();
        }
    }

    private class GroupReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "GroupReceiver.onReceive");
            Group group = intent.getParcelableExtra(HttpRequestService.GROUP);

            if (group != null) {
                mGroup = group;
                setTexts();
                setOnClickListeners();
                loadPicture();
            } else {
                Alerts.showConnectionFailedAlert(GroupActivity.this);
            }
        }
    }
}
