package com.example.tarasantoshchuk.vimeoapp.entity.video;

import android.app.Activity;
import android.os.Bundle;

public class VideoActivity extends Activity {
    private static final String VIDEO = "VIDEO";

    public static Bundle getStartExtras(Video video) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(VIDEO, video);

        return bundle;
    }
}
