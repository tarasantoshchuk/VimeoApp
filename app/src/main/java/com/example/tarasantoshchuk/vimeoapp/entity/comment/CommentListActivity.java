package com.example.tarasantoshchuk.vimeoapp.entity.comment;

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

public class CommentListActivity extends Activity {
    private static final String TAG = CommentListActivity.class.getSimpleName();
    private static final String COMMENT_LIST_REQUEST = "CommentListRequest";
    private static final String TITLE = "Title";

    private static final String LAST_REQUEST = "LastRequest";

    public static Bundle getStartExtras(String title, HttpRequestInfo commentListRequest) {
        Bundle bundle = new Bundle();

        bundle.putString(TITLE, title);
        bundle.putSerializable(COMMENT_LIST_REQUEST, commentListRequest);

        return bundle;
    }

    private String mTitle;

    private TextView mTxtCommentListTitle;
    private ListView mCommentList;
    private Button mBtnCommentListPrev;
    private Button mBtnCommentListNext;

    private CommentListReceiver mReceiver;

    private HttpRequestInfo mLastRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);

        mTitle = getIntent().getStringExtra(TITLE);

        mTxtCommentListTitle = (TextView) findViewById(R.id.txtCommentListTitle);
        mTxtCommentListTitle.setText(mTitle);

        mBtnCommentListPrev = (Button) findViewById(R.id.btnCommentListPrev);
        mBtnCommentListPrev.setEnabled(false);
        mBtnCommentListPrev.setVisibility(View.INVISIBLE);

        mBtnCommentListNext = (Button) findViewById(R.id.btnCommentListNext);
        mBtnCommentListNext.setEnabled(false);
        mBtnCommentListNext.setVisibility(View.INVISIBLE);

        mCommentList = (ListView) findViewById(R.id.listComments);

        mCommentList.setAdapter(new CommentListAdapter(getLayoutInflater(), this));

        mReceiver = new CommentListReceiver();

        if(savedInstanceState != null) {
            mLastRequest = (HttpRequestInfo) savedInstanceState.getSerializable(LAST_REQUEST);
        } else {
            mLastRequest = (HttpRequestInfo) getIntent().getSerializableExtra(COMMENT_LIST_REQUEST);
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

        registerReceiver(mReceiver, HttpRequestService.getCommentListIntentFilter());

        startHttpService(mLastRequest);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

        CommentListAdapter listAdapter = (CommentListAdapter) mCommentList.getAdapter();

        listAdapter.cancelAllTasks();

        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putSerializable(LAST_REQUEST, mLastRequest);
    }

    private class CommentListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "CommentListReceiver.onReceive");

            ArrayList<Comment> list =
                    intent.getParcelableArrayListExtra(HttpRequestService.COMMENT_LIST);

            if(list != null) {
                CommentListAdapter adapter = (CommentListAdapter) mCommentList.getAdapter();

                adapter.updateList(list);

                final HttpRequestInfo nextPage = (HttpRequestInfo)
                        intent.getSerializableExtra(HttpRequestService.NEXT_PAGE);
                setButton(mBtnCommentListNext, nextPage);

                final HttpRequestInfo prevPage = (HttpRequestInfo)
                        intent.getSerializableExtra(HttpRequestService.PREV_PAGE);
                setButton(mBtnCommentListPrev, prevPage);
            } else {
                Alerts.showConnectionFailedAlert(CommentListActivity.this);
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
