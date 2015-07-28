package com.example.tarasantoshchuk.vimeoapp.entity.group;

import android.app.Activity;
import android.os.Bundle;

public class GroupActivity extends Activity {
    private static final String GROUP = "Group";

    public static Bundle getStartExtras(Group group) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(GROUP, group);

        return bundle;
    }
}
