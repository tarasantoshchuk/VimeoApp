package com.example.tarasantoshchuk.vimeoapp.entity.video;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.entity.comment.CommentListActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserListActivity;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;
import com.example.tarasantoshchuk.vimeoapp.util.Alerts;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestInfo;

import java.util.concurrent.TimeUnit;

public class VideoActivity extends Activity {
    private static final String TAG = VideoActivity.class.getSimpleName();

    private static final String VIDEO = "Video";
    private static final String VIDEO_REQUEST = "VideoRequest";
    private static final String STARTUP_EXTRA = "StartupExtra";

    private static final String LIKES_TITLE = "Likes";
    private static final String COMMENTS_TITLE = "Comments";

    private static final long DAYS_IN_MONTH = 30;
    private static final long DAYS_IN_YEAR = 365;

    private static final int DAY_FORMAT_STRING_ID = R.string.txt_day_created_format;
    private static final int MONTH_FORMAT_STRING_ID = R.string.txt_month_created_format;
    private static final int YEAR_FORMAT_STRING_ID = R.string.txt_year_created_format;

    private enum StartupExtra {
        VIDEO, VIDEO_REQUEST
    }

    public static Bundle getStartExtras(Video video) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(STARTUP_EXTRA, StartupExtra.VIDEO);

        bundle.putParcelable(VIDEO, video);

        return bundle;
    }

    public static Bundle getStartExtras(HttpRequestInfo videoRequestInfo) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(STARTUP_EXTRA, StartupExtra.VIDEO_REQUEST);

        bundle.putSerializable(VIDEO_REQUEST, videoRequestInfo);

        return bundle;
    }

    private Video mVideo;

    private WebView mWebView;
    private LinearLayout mWebViewPlaceholder;

    private TextView mTxtVideoName;
    private TextView mTxtVideoCreated;
    private TextView mTxtVideoDescription;

    private TextView mTxtVideoOwnerTitle;
    private TextView mTxtVideoPlaysCountTitle;
    private TextView mTxtVideoLikesCountTitle;
    private TextView mTxtVideoCommentsCountTitle;

    private TextView mTxtVideoOwnerName;
    private TextView mTxtVideoPlaysCount;
    private TextView mTxtVideoLikesCount;
    private TextView mTxtVideoCommentsCount;

    private VideoReceiver mReceiver;

    private HttpRequestInfo mRequestInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mReceiver = new VideoReceiver();

        initViews();

        StartupExtra startupType = (StartupExtra)
                getIntent().getSerializableExtra(STARTUP_EXTRA);

        if (startupType == StartupExtra.VIDEO) {
            mVideo = getIntent().getParcelableExtra(VIDEO);

            setTexts();
            setOnClickListeners();
            setWebView();
        } else {
            mRequestInfo = (HttpRequestInfo)
                    getIntent().getSerializableExtra(VIDEO_REQUEST);
        }
    }

    private void startHttpService(HttpRequestInfo requestInfo) {
        Log.d(TAG, "startHttpService");

        Intent videoRequestIntent = new Intent(this, HttpRequestService.class);

        videoRequestIntent.putExtras(HttpRequestService.getStartExtras(requestInfo));

        startService(videoRequestIntent);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

        registerReceiver(mReceiver, HttpRequestService.getVideoIntentFilter());

        if(mVideo == null) {
            startHttpService(mRequestInfo);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        if(mWebView != null) {
            mWebViewPlaceholder.removeView(mWebView);
        }

        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_video);

        initViews();
        setTexts();
        setOnClickListeners();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

        unregisterReceiver(mReceiver);
    }

    private void initViews() {
        mWebViewPlaceholder = (LinearLayout) findViewById(R.id.webViewPlaceholder);
        if(mWebView == null) {
            mWebView = new WebView(this);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mWebViewPlaceholder.addView(mWebView, 0, layoutParams);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mTxtVideoName = (TextView) findViewById(R.id.txtVideoName);
            mTxtVideoCreated = (TextView) findViewById(R.id.txtVideoCreated);
            mTxtVideoDescription = (TextView) findViewById(R.id.txtVideoDescription);

            mTxtVideoOwnerTitle = (TextView) findViewById(R.id.txtVideoOwnerTitle);
            mTxtVideoPlaysCountTitle = (TextView) findViewById(R.id.txtVideoPlaysCountTitle);
            mTxtVideoLikesCountTitle = (TextView) findViewById(R.id.txtVideoLikesCountTitle);
            mTxtVideoCommentsCountTitle = (TextView) findViewById(R.id.txtVideoCommentsCountTitle);

            mTxtVideoOwnerName = (TextView) findViewById(R.id.txtVideoOwnerName);
            mTxtVideoPlaysCount = (TextView) findViewById(R.id.txtVideoPlaysCount);
            mTxtVideoLikesCount = (TextView) findViewById(R.id.txtVideoLikesCount);
            mTxtVideoCommentsCount = (TextView) findViewById(R.id.txtVideoCommentsCount);
        }
    }

    private void setTexts() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mTxtVideoName.setText(mVideo.getName());
            mTxtVideoCreated.setText(getVideoTimestamp());
            mTxtVideoDescription.setText(mVideo.getDescription());

            mTxtVideoOwnerName.setText(mVideo.getOwner().getName());
            mTxtVideoPlaysCount.setText(Integer.toString(mVideo.getPlayCount()));
            mTxtVideoLikesCount.setText(Integer.toString(mVideo.getLikesCount()));
            mTxtVideoCommentsCount.setText(Integer.toString(mVideo.getCommentsCount()));
        }
    }

    private void setOnClickListeners() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mTxtVideoOwnerTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ownerIntent = new Intent(VideoActivity.this, UserActivity.class);

                    HttpRequestInfo ownerRequest = HttpRequestInfo
                            .getUserInfoRequest(mVideo.getOwner().getId());

                    ownerIntent.putExtras(UserActivity.getStartExtras(ownerRequest));

                    Log.d(TAG, "start UserActivity: video's owner");
                    startActivity(ownerIntent);
                }
            });

            mTxtVideoLikesCountTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mVideo.getLikesCount() != 0) {
                        Intent likesIntent =
                                new Intent(VideoActivity.this, UserListActivity.class);

                        HttpRequestInfo likesRequest =
                                HttpRequestInfo.getVideoLikesRequest(mVideo.getId());

                        likesIntent.putExtras(UserListActivity
                                .getStartExtras(LIKES_TITLE, likesRequest));

                        Log.d(TAG, "start UserListActivity: video's likes");
                        startActivity(likesIntent);
                    } else {
                        Toast.makeText(VideoActivity.this, getString(R.string.txt_no_likes),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mTxtVideoCommentsCountTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mVideo.getCommentsCount() != 0) {
                        Intent commentsIntent =
                                new Intent(VideoActivity.this, CommentListActivity.class);

                        HttpRequestInfo commentsRequest = HttpRequestInfo
                                .getCommentsRequest(mVideo.getId());

                        commentsIntent.putExtras(CommentListActivity
                                .getStartExtras(COMMENTS_TITLE, commentsRequest));

                        Log.d(TAG, "start CommentListActivity: video's comments");
                        startActivity(commentsIntent);
                    } else {
                        Toast.makeText(VideoActivity.this, getString(R.string.txt_no_comments),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setWebView() {
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // ignore user pressed link on webview
            }
        });

        mWebView.loadData(mVideo.getEmbedHtml(), "text/html", null);
    }

    private String getVideoTimestamp() {
        long createdMillisCount = System.currentTimeMillis() - mVideo.getDateCreated().getTime();

        long createdDaysCount = TimeUnit.DAYS.convert(createdMillisCount, TimeUnit.MILLISECONDS);

        if(createdDaysCount < DAYS_IN_MONTH) {
            return String.format(getString(DAY_FORMAT_STRING_ID), createdDaysCount);
        } else if (createdDaysCount < DAYS_IN_YEAR) {
            return String.format(getString(MONTH_FORMAT_STRING_ID), createdDaysCount / DAYS_IN_MONTH);
        } else {
            return String.format(getString(YEAR_FORMAT_STRING_ID), createdDaysCount / DAYS_IN_YEAR);
        }
    }

    private class VideoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "VideoReceiver.onReceive");
            Video video = intent.getParcelableExtra(HttpRequestService.VIDEO_RESULT_KEY);

            if(video != null) {
                mVideo = video;
                setTexts();
                setOnClickListeners();
                setWebView();
            } else {
                Alerts.showConnectionFailedAlert(VideoActivity.this);
            }
        }
    }
}
