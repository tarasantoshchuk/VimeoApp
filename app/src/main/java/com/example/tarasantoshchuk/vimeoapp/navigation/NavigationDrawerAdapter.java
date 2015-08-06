package com.example.tarasantoshchuk.vimeoapp.navigation;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.activity.LogoutActivity;
import com.example.tarasantoshchuk.vimeoapp.activity.SearchActivity;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserActivity;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestInfo;

public class NavigationDrawerAdapter extends ArrayAdapter<String> {
    private static final String TAG = NavigationDrawerAdapter.class.getSimpleName();

    private static final int POSITION_LOGGED_USER_VIEW = 0;
    private static final int POSITION_SEARCH = 1;
    private static final int POSITION_LOGOUT = 2;

    private Activity mCurrentActivity;

    public NavigationDrawerAdapter(Activity currentActivity) {

        super(currentActivity.getApplicationContext(), android.R.layout.simple_list_item_1,
                currentActivity.getResources().getStringArray(R.array.navigation_names));

        mCurrentActivity = currentActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView");

        View view = super.getView(position, convertView, parent);

        switch(position) {
            case POSITION_LOGGED_USER_VIEW:
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLoggedUserActivity();
                    }
                });
                break;
            case POSITION_SEARCH:
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSearchActivity();
                    }
                });
                break;
            case POSITION_LOGOUT:
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLogoutActivity();
                    }
                });
                break;
            default:
                Log.wtf(TAG, "getView: unexpected position value");
        }

        return view;
    }

    private void startLogoutActivity() {
        Intent logoutActivity = new Intent(mCurrentActivity, LogoutActivity.class);

        mCurrentActivity.startActivity(logoutActivity);
    }

    private void startSearchActivity() {
        Intent searchIntent = new Intent(mCurrentActivity, SearchActivity.class);

        mCurrentActivity.startActivity(searchIntent);
    }

    private void startLoggedUserActivity() {
        Intent loggedUserIntent = new Intent(mCurrentActivity, UserActivity.class);

        HttpRequestInfo loggedUserRequest = HttpRequestInfo.getMyInfoRequest();

        loggedUserIntent.putExtras(UserActivity.getStartExtras(loggedUserRequest));

        mCurrentActivity.startActivity(loggedUserIntent);
    }
}
