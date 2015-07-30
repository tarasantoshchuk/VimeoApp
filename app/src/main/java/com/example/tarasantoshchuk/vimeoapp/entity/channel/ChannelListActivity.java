package com.example.tarasantoshchuk.vimeoapp.entity.channel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;
import com.example.tarasantoshchuk.vimeoapp.util.Alerts;
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;

import java.util.ArrayList;

public class ChannelListActivity extends Activity {
    private static final String CHANNEL_LIST_REQUEST = "ChannelListRequest";
    private static final String TITLE = "Title";

    private static final String LAST_REQUEST = "LastRequest";

    public static Bundle getStartExtras(String title, HttpRequestInfo channelListRequest) {
        Bundle bundle = new Bundle();

        bundle.putString(TITLE, title);
        bundle.putSerializable(CHANNEL_LIST_REQUEST, channelListRequest);

        return bundle;
    }

    private String mTitle;

    private TextView mTxtChannelListTitle;
    private ListView mChannelList;
    private Button mBtnChannelListPrev;
    private Button mBtnChannelListNext;

    private ChannelListReceiver mReceiver;

    private HttpRequestInfo mLastRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

        mTitle = getIntent().getStringExtra(TITLE);

        mTxtChannelListTitle = (TextView) findViewById(R.id.txtChannelListTitle);
        mTxtChannelListTitle.setText(mTitle);

        mBtnChannelListPrev = (Button) findViewById(R.id.btnChannelListPrev);
        mBtnChannelListPrev.setEnabled(false);
        mBtnChannelListPrev.setVisibility(View.INVISIBLE);

        mBtnChannelListNext = (Button) findViewById(R.id.btnChannelListNext);
        mBtnChannelListNext.setEnabled(false);
        mBtnChannelListNext.setVisibility(View.INVISIBLE);

        mChannelList = (ListView) findViewById(R.id.listChannels);

        mChannelList.setAdapter(new ChannelListAdapter(getLayoutInflater(), this));

        mReceiver = new ChannelListReceiver();

        if(savedInstanceState != null) {
            mLastRequest = (HttpRequestInfo) savedInstanceState.getSerializable(LAST_REQUEST);
        } else {
            mLastRequest = (HttpRequestInfo) getIntent().getSerializableExtra(CHANNEL_LIST_REQUEST);
        }
    }

    private void startHttpService(HttpRequestInfo requestInfo) {
        Intent httpServiceIntent = new Intent(this, HttpRequestService.class);

        httpServiceIntent.putExtras(HttpRequestService.getStartExtras(requestInfo));

        startService(httpServiceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(mReceiver, HttpRequestService.getChannelListIntentFilter());

        startHttpService(mLastRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();

        ChannelListAdapter listAdapter = (ChannelListAdapter) mChannelList.getAdapter();

        listAdapter.cancelAllTasks();

        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(LAST_REQUEST, mLastRequest);
    }

    private class ChannelListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Channel> list =
                    intent.getParcelableArrayListExtra(HttpRequestService.CHANNEL_LIST);

            if (list != null) {
                ChannelListAdapter adapter = (ChannelListAdapter) mChannelList.getAdapter();

                adapter.updateList(list);

                final HttpRequestInfo nextPage = (HttpRequestInfo)
                        intent.getSerializableExtra(HttpRequestService.NEXT_PAGE);
                setButton(mBtnChannelListNext, nextPage);

                final HttpRequestInfo prevPage = (HttpRequestInfo)
                        intent.getSerializableExtra(HttpRequestService.PREV_PAGE);
                setButton(mBtnChannelListPrev, prevPage);
            } else {
                Alerts.showConnectionFailedAlert(ChannelListActivity.this);
            }
        }

        private void setButton(Button button, final HttpRequestInfo request) {
            if(request != null) {
                button.setEnabled(true);
                button.setVisibility(View.VISIBLE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startHttpService(request);
                        mLastRequest = request;
                    }
                });
            } else {
                button.setEnabled(false);
                button.setVisibility(View.INVISIBLE);
            }
        }
    }
}
