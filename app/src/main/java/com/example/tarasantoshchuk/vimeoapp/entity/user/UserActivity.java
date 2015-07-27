package com.example.tarasantoshchuk.vimeoapp.entity.user;

import android.app.Activity;
import android.os.Bundle;

public class UserActivity extends Activity {
    private static final String USER = "User";

    public static Bundle getStartExtras(User user) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(USER, user);

        return bundle;
    }
}
