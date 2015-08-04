package com.example.tarasantoshchuk.vimeoapp.entity.group;

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
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;
import com.example.tarasantoshchuk.vimeoapp.util.Alerts;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestInfo;

import java.util.ArrayList;

public class GroupListActivity extends Activity {
    private static final String TAG = GroupListActivity.class.getSimpleName();

    private static final String GROUP_LIST_REQUEST = "GroupListRequest";
    private static final String TITLE = "Title";

    private static final String LAST_REQUEST = "LastRequest";

    public static Bundle getStartExtras(String title, HttpRequestInfo groupListRequest) {
        Bundle bundle = new Bundle();

        bundle.putString(TITLE, title);
        bundle.putSerializable(GROUP_LIST_REQUEST, groupListRequest);

        return bundle;
    }

    private String mTitle;

    private TextView mTxtGroupListTitle;
    private ListView mGroupList;
    private Button mBtnGroupListPrev;
    private Button mBtnGroupListNext;

    private GroupListReceiver mReceiver;

    private HttpRequestInfo mLastRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        mTitle = getIntent().getStringExtra(TITLE);

        mTxtGroupListTitle = (TextView) findViewById(R.id.txtGroupListTitle);
        mTxtGroupListTitle.setText(mTitle);

        mBtnGroupListPrev = (Button) findViewById(R.id.btnGroupListPrev);
        mBtnGroupListPrev.setEnabled(false);
        mBtnGroupListPrev.setVisibility(View.INVISIBLE);

        mBtnGroupListNext = (Button) findViewById(R.id.btnGroupListNext);
        mBtnGroupListNext.setEnabled(false);
        mBtnGroupListNext.setVisibility(View.INVISIBLE);

        mGroupList = (ListView) findViewById(R.id.listGroups);

        mGroupList.setAdapter(new GroupListAdapter(getLayoutInflater(), this));

        mReceiver = new GroupListReceiver();

        if(savedInstanceState != null) {
            mLastRequest = (HttpRequestInfo) savedInstanceState.getSerializable(LAST_REQUEST);
        } else {
            mLastRequest = (HttpRequestInfo) getIntent().getSerializableExtra(GROUP_LIST_REQUEST);
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

        registerReceiver(mReceiver, HttpRequestService.getGroupListIntentFilter());

        startHttpService(mLastRequest);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

        GroupListAdapter listAdapter = (GroupListAdapter) mGroupList.getAdapter();

        listAdapter.cancelAllTasks();

        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putSerializable(LAST_REQUEST, mLastRequest);
    }

    private class GroupListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "GroupListReceiver.onReceive");

            ArrayList<Group> list =
                    intent.getParcelableArrayListExtra(HttpRequestService.GROUP_LIST);

            if(list != null) {

                GroupListAdapter adapter = (GroupListAdapter) mGroupList.getAdapter();

                adapter.updateList(list);

                final HttpRequestInfo nextPage = (HttpRequestInfo)
                        intent.getSerializableExtra(HttpRequestService.NEXT_PAGE);
                setButton(mBtnGroupListNext, nextPage);

                final HttpRequestInfo prevPage = (HttpRequestInfo)
                        intent.getSerializableExtra(HttpRequestService.PREV_PAGE);
                setButton(mBtnGroupListPrev, prevPage);
            } else {
                Alerts.showConnectionFailedAlert(GroupListActivity.this);
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
