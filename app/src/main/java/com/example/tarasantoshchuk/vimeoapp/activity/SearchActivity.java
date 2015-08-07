package com.example.tarasantoshchuk.vimeoapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.entity.channel.ChannelListActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.group.GroupListActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserListActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.video.VideoListActivity;
import com.example.tarasantoshchuk.vimeoapp.navigation.NavigationDrawerAdapter;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestInfo;

public class SearchActivity extends Activity {
    private static final String TAG = SearchActivity.class.getSimpleName();

    private static final int EMPTY_SELECTION = -1;

    private static final int SEARCH_RESULT_TITLE_ID = R.string.txt_search_result;

    private RadioGroup mRgSearchType;
    private Button mBtnFind;
    private EditText mEdtQuery;

    private DrawerLayout mDrawerLayout;
    private ListView mLeftDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mBtnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = mRgSearchType.getCheckedRadioButtonId();
                String query = mEdtQuery.getText().toString();

                if(TextUtils.isEmpty(query)) {
                    Toast.makeText(SearchActivity.this, getString(R.string.txt_enter_search_name),
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                switch (id) {
                    case R.id.rBtnUsers:
                        startUsersSearch(query);
                        break;
                    case R.id.rBtnVideos:
                        startVideosSearch(query);
                        break;
                    case R.id.rBtnGroups:
                        startGroupsSearch(query);
                        break;
                    case R.id.rBtnChannels:
                        startChannelsSearch(query);
                        break;
                    case EMPTY_SELECTION:
                        Toast.makeText(SearchActivity.this,
                                getString(R.string.txt_choose_search_type), Toast.LENGTH_SHORT)
                                .show();
                        break;
                    default:
                        Log.wtf(TAG, "wrong id returned by RadioGroup.getCheckedRadioButtonId()");
                }
            }
        });
    }

    private void startChannelsSearch(String query) {
        HttpRequestInfo channelsSearchRequest = HttpRequestInfo.getChannelSearchRequest(query);

        Intent channelsSearchIntent = new Intent(this, ChannelListActivity.class);

        channelsSearchIntent.putExtras(ChannelListActivity
                .getStartExtras(getString(SEARCH_RESULT_TITLE_ID), channelsSearchRequest));

        startActivity(channelsSearchIntent);
    }

    private void startGroupsSearch(String query) {
        HttpRequestInfo groupsSearchRequest = HttpRequestInfo.getGroupSearchRequest(query);

        Intent groupsSearchIntent = new Intent(this, GroupListActivity.class);

        groupsSearchIntent.putExtras(GroupListActivity
                .getStartExtras(getString(SEARCH_RESULT_TITLE_ID), groupsSearchRequest));

        startActivity(groupsSearchIntent);
    }

    private void startVideosSearch(String query) {
        HttpRequestInfo videosSearchRequest = HttpRequestInfo.getVideoSearchRequest(query);

        Intent videosSearchIntent = new Intent(this, VideoListActivity.class);

        videosSearchIntent.putExtras(VideoListActivity
                .getStartExtras(getString(SEARCH_RESULT_TITLE_ID), videosSearchRequest));

        startActivity(videosSearchIntent);
    }

    private void startUsersSearch(String query) {
        HttpRequestInfo usersSearchRequest = HttpRequestInfo.getUserSearchRequest(query);

        Intent usersSearchIntent = new Intent(this, UserListActivity.class);

        usersSearchIntent.putExtras(UserListActivity
                .getStartExtras(getString(SEARCH_RESULT_TITLE_ID), usersSearchRequest));

        startActivity(usersSearchIntent);
    }

    private void initViews() {
        mRgSearchType = (RadioGroup) findViewById(R.id.rgSearchType);
        mBtnFind = (Button) findViewById(R.id.btnFind);
        mEdtQuery = (EditText) findViewById(R.id.edtQuery);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                hideKeyboard();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        mLeftDrawer = (ListView) findViewById(R.id.leftDrawer);
        mLeftDrawer.setAdapter(new NavigationDrawerAdapter(this));

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if(view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
