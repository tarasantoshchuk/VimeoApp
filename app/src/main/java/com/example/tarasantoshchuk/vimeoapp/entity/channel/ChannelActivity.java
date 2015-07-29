package com.example.tarasantoshchuk.vimeoapp.entity.channel;

import android.app.Activity;
import android.os.Bundle;

public class ChannelActivity extends Activity {
    private static final String CHANNEL = "Channel";

    public static Bundle getStartExtras(Channel channel) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(CHANNEL, channel);

        return bundle;
    }
}
