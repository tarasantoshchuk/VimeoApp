package com.example.tarasantoshchuk.vimeoapp.entity.video;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.navigation.NavigationDrawerAdapter;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;
import com.example.tarasantoshchuk.vimeoapp.util.Alerts;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestInfo;

public class VideoListActivity extends Activity {
    private static final String TAG = VideoListActivity.class.getSimpleName();

    private static final String VIDEO_LIST_REQUEST = "VideoListRequest";
    private static final String TITLE = "Title";
    private static final String LAST_REQUEST = "LastRequest";

    public static Bundle getStartExtras(String title, HttpRequestInfo videoListRequest) {
        Bundle bundle = new Bundle();

        bundle.putString(TITLE, title);
        bundle.putSerializable(VIDEO_LIST_REQUEST, videoListRequest);

        return bundle;
    }

    private String mTitle;

    private TextView mTxtVideoListTitle;
    private ListView mVideoList;
    private Button mBtnVideoListPrev;
    private Button mBtnVideoListNext;

    private ListView mLeftDrawer;

    private VideoListReceiver mReceiver;

    private HttpRequestInfo mLastRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        mTitle = getIntent().getStringExtra(TITLE);

        mTxtVideoListTitle = (TextView) findViewById(R.id.txtVideoListTitle);
        mTxtVideoListTitle.setText(mTitle);

        mBtnVideoListPrev = (Button) findViewById(R.id.btnVideoListPrev);
        mBtnVideoListPrev.setEnabled(false);
        mBtnVideoListPrev.setVisibility(View.INVISIBLE);

        mBtnVideoListNext = (Button) findViewById(R.id.btnVideoListNext);
        mBtnVideoListNext.setEnabled(false);
        mBtnVideoListNext.setVisibility(View.INVISIBLE);

        mVideoList = (ListView) findViewById(R.id.listVideos);
        mVideoList.setAdapter(new VideoListAdapter(getLayoutInflater(), this));

        mLeftDrawer = (ListView) findViewById(R.id.leftDrawer);
        mLeftDrawer.setAdapter(new NavigationDrawerAdapter(this));

        mReceiver = new VideoListReceiver();

        if(savedInstanceState != null) {
            mLastRequest = (HttpRequestInfo) savedInstanceState.getSerializable(LAST_REQUEST);
        } else {
            mLastRequest = (HttpRequestInfo) getIntent().getSerializableExtra(VIDEO_LIST_REQUEST);
        }
    }

    private void startHttpService(HttpRequestInfo requestInfo) {
        Log.d(TAG, "startHttpService");
        Intent httpServiceIntent = new Intent(this, HttpRequestService.class);

        httpServiceIntent.putExtras(HttpRequestService.getStartExtras(requestInfo));

        startService(httpServiceIntent);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

        registerReceiver(mReceiver, HttpRequestService.getVideoListIntentFilter());

        startHttpService(mLastRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putSerializable(LAST_REQUEST, mLastRequest);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

        VideoListAdapter adapter = (VideoListAdapter) mVideoList.getAdapter();
        adapter.cancelAllTasks();

        unregisterReceiver(mReceiver);
    }

    private class VideoListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "VideoListReceiver.onReceive");

            VideoList list = intent.getParcelableExtra(HttpRequestService.KEY_VIDEO_LIST_RESULT);

            if(list != null) {
                VideoListAdapter adapter = (VideoListAdapter) mVideoList.getAdapter();

                adapter.updateList(list);

                final HttpRequestInfo nextPage = (HttpRequestInfo)
                        intent.getSerializableExtra(HttpRequestService.KEY_NEXT_PAGE_RESULT);
                setButton(mBtnVideoListNext, nextPage);

                final HttpRequestInfo prevPage = (HttpRequestInfo)
                        intent.getSerializableExtra(HttpRequestService.KEY_PREV_PAGE_RESULT);
                setButton(mBtnVideoListPrev, prevPage);
            } else {
                Alerts.showConnectionFailedAlert(VideoListActivity.this);
            }
        }

        private void setButton(Button button, final HttpRequestInfo requestInfo) {
            if(requestInfo != null) {
                button.setEnabled(true);
                button.setVisibility(View.VISIBLE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startHttpService(requestInfo);
                        mLastRequest = requestInfo;
                    }
                });
            } else {
                button.setEnabled(false);
                button.setVisibility(View.INVISIBLE);
            }
        }
    }
}
