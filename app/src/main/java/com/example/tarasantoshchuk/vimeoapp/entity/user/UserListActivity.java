package com.example.tarasantoshchuk.vimeoapp.entity.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;

public class UserListActivity extends Activity {
    private static final String USER_LIST_REQUEST = "UserListRequest";
    private static final String TITLE = "Title";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mTitle = getIntent().getStringExtra(TITLE);

        mTxtUserListTitle = (TextView) findViewById(R.id.txtUserListTitle);
        mTxtUserListTitle.setText(mTitle);

        mBtnUserListPrev = (Button) findViewById(R.id.btnUserListPrev);
        mBtnUserListPrev.setEnabled(false);
        mBtnUserListPrev.setVisibility(View.INVISIBLE);

        mBtnUserListNext = (Button) findViewById(R.id.btnUserListNext);
        mBtnUserListNext.setEnabled(false);
        mBtnUserListNext.setVisibility(View.INVISIBLE);

        mUserList = (ListView) findViewById(R.id.listUsers);

        mUserList.setAdapter(new UserListAdapter(getLayoutInflater(), this));

        startHttpService((HttpRequestInfo) getIntent().getSerializableExtra(USER_LIST_REQUEST));
    }

    private void startHttpService(HttpRequestInfo requestInfo) {
        UserListReceiver receiver = new UserListReceiver();

        registerReceiver(receiver, HttpRequestService.getUserListIntentFilter());

        Intent httpServiceIntent = new Intent(this, HttpRequestService.class);

        httpServiceIntent.putExtras(HttpRequestService.getStartExtras(requestInfo));

        startService(httpServiceIntent);
    }

    @Override
    protected void onStop() {
        UserListAdapter listAdapter = (UserListAdapter) mUserList.getAdapter();

        listAdapter.cancelAllTasks();
    }

    private class UserListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            UserList list = intent.getParcelableExtra(HttpRequestService.USER_LIST);

            UserListAdapter adapter = (UserListAdapter) mUserList.getAdapter();

            adapter.updateList(list);

            final HttpRequestInfo nextPage = list.getNextPageRequest();
            setButton(mBtnUserListNext, nextPage);

            final HttpRequestInfo prevPage = list.getPrevPageRequest();
            setButton(mBtnUserListPrev, prevPage);
        }

        private void setButton(Button button, final HttpRequestInfo nextPage) {
            if(nextPage != null) {
                button.setEnabled(true);
                button.setVisibility(View.VISIBLE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startHttpService(nextPage);
                    }
                });
            } else {
                button.setEnabled(false);
                button.setVisibility(View.INVISIBLE);
            }
        }
    }
}
