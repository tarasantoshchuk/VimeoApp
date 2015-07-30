package com.example.tarasantoshchuk.vimeoapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.tarasantoshchuk.vimeoapp.R;

public class Alerts {
    public static void showConnectionFailedAlert(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false)
                .setTitle(activity.getString(R.string.txt_connection_fail))
                .setMessage(activity.getString(R.string.txt_check_connection))
                .setCancelable(false)
                .setNeutralButton(activity.getString(R.string.txt_close),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                            }
                        })
                .show();
    }

    public static void showAuthorizationFailedAlert(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false)
                .setTitle(activity.getString(R.string.txt_auth_fail))
                .setMessage(activity.getString(R.string.txt_auth_fail_msg))
                .setCancelable(false)
                .setNeutralButton(activity.getString(R.string.txt_close),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                            }
                        })
                .show();
    }
}
