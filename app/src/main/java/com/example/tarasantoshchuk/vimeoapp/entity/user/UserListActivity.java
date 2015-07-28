package com.example.tarasantoshchuk.vimeoapp.entity.user;

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
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;

public class UserListActivity extends Activity {
    private static final String USER_LIST_REQUEST = "UserListRequest";
    private static final String TITLE = "Title";

    private static final String LAST_REQUEST = "LastRequest";

    public static Bundle getStartExtras(String title, HttpRequestInfo userListRequest) {
        Bundle bundle = new Bundle();

        bundle.putString(TITLE, title);
        bundle.putSerializable(USER_LIST_REQUEST, userListRequest);

        return bundle;
    }

    private String mTitle;

    private TextView mTxtUserListTitle;
    private ListView mUserList;
    private Button mBtnUserListPrev;
    private Button mBtnUserListNext;

    private UserListReceiver mReceiver;

    private HttpRequestInfo mLastRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mTitle = getIntent().getStringExtra(TITLE);

        mTxtUserListTitle = (TextView) findViewById(R.id.txtUserListTitle);
        mTxtUserListTitle.setText(mTitle);

        mBtnUserListPrev = (Button) findViewById(R.id.btnGroupListPrev);
        mBtnUserListPrev.setEnabled(false);
        mBtnUserListPrev.setVisibility(View.INVISIBLE);

        mBtnUserListNext = (Button) findViewById(R.id.btnGroupListNext);
        mBtnUserListNext.setEnabled(false);
        mBtnUserListNext.setVisibility(View.INVISIBLE);

        mUserList = (ListView) findViewById(R.id.listUsers);

        mUserList.setAdapter(new UserListAdapter(getLayoutInflater(), this));

        mReceiver = new UserListReceiver();

        if(savedInstanceState != null) {
            mLastRequest = (HttpRequestInfo) savedInstanceState.getSerializable(LAST_REQUEST);
        } else {
            mLastRequest = (HttpRequestInfo) getIntent().getSerializableExtra(USER_LIST_REQUEST);
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

        registerReceiver(mReceiver, HttpRequestService.getUserListIntentFilter());

        startHttpService(mLastRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(LAST_REQUEST, mLastRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();

        UserListAdapter listAdapter = (UserListAdapter) mUserList.getAdapter();

        listAdapter.cancelAllTasks();

        unregisterReceiver(mReceiver);
    }


    private class UserListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            UserList list = intent.getParcelableExtra(HttpRequestService.USER_LIST);

            UserListAdapter adapter = (UserListAdapter) mUserList.getAdapter();

            adapter.updateList(list);

            final HttpRequestInfo nextPage = (HttpRequestInfo)
                    intent.getSerializableExtra(HttpRequestService.NEXT_PAGE);
            setButton(mBtnUserListNext, nextPage);

            final HttpRequestInfo prevPage = (HttpRequestInfo)
                    intent.getSerializableExtra(HttpRequestService.PREV_PAGE);
            setButton(mBtnUserListPrev, prevPage);
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