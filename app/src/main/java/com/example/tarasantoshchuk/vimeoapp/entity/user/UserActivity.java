package com.example.tarasantoshchuk.vimeoapp.entity.user;

import android.app.Activity;
import android.os.Bundle;

import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;

public class UserActivity extends Activity {
    private static final String USER = "User";
    private static final String USER_REQUEST = "UserRequest";
    private static final String STARTUP_EXTRA = "StartupExtra";

    private enum StartupExtra {
        USER, USER_REQUEST
    }

    public static Bundle getStartExtras(User user) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(STARTUP_EXTRA, StartupExtra.USER);

        bundle.putParcelable(USER, user);

        return bundle;
    }

    public static Bundle getStartExtras(HttpRequestInfo userRequestInfo) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(STARTUP_EXTRA, StartupExtra.USER_REQUEST);

        bundle.putSerializable(USER_REQUEST, userRequestInfo);

        return bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
