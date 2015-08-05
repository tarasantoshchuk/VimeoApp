package com.example.tarasantoshchuk.vimeoapp.entity.channel;

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

public class ChannelActivity extends Activity {
    private static final String TAG = ChannelActivity.class.getSimpleName();

    private static final String CHANNEL = "Channel";
    private static final String CHANNEL_REQUEST = "ChannelRequest";
    private static final String STARTUP_EXTRA = "StartupExtra";

    private static final String SUBSCRIBERS_TITLE = "Subscribers";
    private static final String VIDEOS_TITLE = "Channel's videos";

    private enum StartupExtra {
        CHANNEL, CHANNEL_REQUEST
    }

    public static Bundle getStartExtras(Channel channel) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(STARTUP_EXTRA, StartupExtra.CHANNEL);

        bundle.putParcelable(CHANNEL, channel);

        return bundle;
    }

    public static Bundle getStartExtras(HttpRequestInfo channelRequestInfo) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(STARTUP_EXTRA, StartupExtra.CHANNEL_REQUEST);

        bundle.putSerializable(CHANNEL_REQUEST, channelRequestInfo);

        return bundle;
    }

    private Channel mChannel;

    private ImageView mImgChannelPicture;

    private TextView mTxtChannelName;
    private TextView mTxtChannelCreated;
    private TextView mTxtChannelDescription;

    private TextView mTxtChannelOwnerTitle;
    private TextView mTxtChannelUsersTitle;
    private TextView mTxtChannelVideosTitle;

    private TextView mTxtChannelOwnerName;
    private TextView mTxtChannelUsersCount;
    private TextView mTxtChannelVideosCount;

    private ChannelReceiver mReceiver;

    private HttpRequestInfo mRequestInfo;

    private AsyncTask<Void, Void, Bitmap> mLoadPictureTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        mReceiver = new ChannelReceiver();

        setViews();

        StartupExtra startupType = (StartupExtra)
                getIntent().getSerializableExtra(STARTUP_EXTRA);

        if(startupType == StartupExtra.CHANNEL) {
            mChannel = getIntent().getParcelableExtra(CHANNEL);

            setTexts();
            setOnClickListeners();
            loadPicture();
        } else {
            mRequestInfo = (HttpRequestInfo)
                    getIntent().getSerializableExtra(CHANNEL_REQUEST);
        }
    }

    private void startHttpService (HttpRequestInfo requestInfo) {
        Log.d(TAG, "startHttpService");

        Intent channelRequestIntent = new Intent(this, HttpRequestService.class);

        channelRequestIntent.putExtras(HttpRequestService.getStartExtras(requestInfo));

        startService(channelRequestIntent);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

        registerReceiver(mReceiver, HttpRequestService.getChannelIntentFilter());

        if(mChannel == null) {
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
        mImgChannelPicture = (ImageView) findViewById(R.id.imgChannelPicture);

        mTxtChannelName = (TextView) findViewById(R.id.txtChannelName);
        mTxtChannelCreated = (TextView) findViewById(R.id.txtChannelCreated);
        mTxtChannelDescription = (TextView) findViewById(R.id.txtChannelDescription);

        mTxtChannelOwnerTitle = (TextView) findViewById(R.id.txtChannelOwnerTitle);
        mTxtChannelUsersTitle = (TextView) findViewById(R.id.txtChannelUsersTitle);
        mTxtChannelVideosTitle = (TextView) findViewById(R.id.txtChannelVideosTitle);

        mTxtChannelOwnerName = (TextView) findViewById(R.id.txtChannelOwnerName);
        mTxtChannelUsersCount = (TextView) findViewById(R.id.txtChannelUsersCount);
        mTxtChannelVideosCount = (TextView) findViewById(R.id.txtChannelVideosCount);
    }

    private void setTexts() {
        mTxtChannelName.setText(mChannel.getName());
        mTxtChannelCreated.setText(mChannel.getCreatedString());
        mTxtChannelDescription.setText(mChannel.getDescription());

        mTxtChannelOwnerName.setText(mChannel.getOwner().getName());
        mTxtChannelUsersCount.setText(Integer.toString(mChannel.getUsersCount()));
        mTxtChannelVideosCount.setText(Integer.toString(mChannel.getVideosCount()));
    }

    private void setOnClickListeners() {
        mTxtChannelOwnerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ownerIntent = new Intent(ChannelActivity.this, UserActivity.class);

                HttpRequestInfo ownerRequest = HttpRequestInfo
                        .getUserInfoRequest(mChannel.getOwner().getId());

                ownerIntent.putExtras(UserActivity.getStartExtras(ownerRequest));

                Log.d(TAG, "start UserActivity: channel's owner");
                startActivity(ownerIntent);
            }
        });

        mTxtChannelUsersTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChannel.getUsersCount() != 0) {
                    Intent subscribersIntent =
                            new Intent(ChannelActivity.this, UserListActivity.class);

                    HttpRequestInfo subscribersRequest =
                            HttpRequestInfo.getChannelUsersRequest(mChannel.getId());

                    subscribersIntent.putExtras(UserListActivity
                            .getStartExtras(SUBSCRIBERS_TITLE, subscribersRequest));

                    Log.d(TAG, "start UserListActivity: channel's subscribers");
                    startActivity(subscribersIntent);
                } else {
                    Toast.makeText(ChannelActivity.this, getString(R.string.txt_no_subscribers),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        mTxtChannelVideosTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChannel.getVideosCount() != 0) {
                    Intent videosIntent =
                            new Intent(ChannelActivity.this, VideoListActivity.class);

                    HttpRequestInfo videosRequest = HttpRequestInfo
                            .getChannelVideosRequest(mChannel.getId());

                    videosIntent.putExtras(VideoListActivity
                            .getStartExtras(VIDEOS_TITLE, videosRequest));

                    Log.d(TAG, "start VideoListActivity: channel's videos");
                    startActivity(videosIntent);
                } else {
                    Toast.makeText(ChannelActivity.this, getString(R.string.txt_no_subscribers),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadPicture() {
        if(mChannel.isPictureLoaded()) {
            mImgChannelPicture.setImageBitmap(mChannel.getPicture());
        } else {

            mLoadPictureTask = (new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {

                    try {

                        URL url = new URL(mChannel.getPictureUrl());
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

                        Toast.makeText(ChannelActivity.this,
                                getString(R.string.txt_bitmap_load_fail),
                                Toast.LENGTH_LONG).show();

                    } else {
                        mImgChannelPicture.setImageBitmap(bitmap);
                        mChannel.setPicture(bitmap);
                    }
                }
            }).execute();
        }
    }

    private class ChannelReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "ChannelReceiver.onReceive");
            Channel channel = intent.getParcelableExtra(HttpRequestService.CHANNEL_RESULT_KEY);

            if(channel != null) {
                mChannel = channel;
                setTexts();
                setOnClickListeners();
                loadPicture();
            } else {
                Alerts.showConnectionFailedAlert(ChannelActivity.this);
            }
        }
    }
}
