package com.example.tarasantoshchuk.vimeoapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.util.AuthorizationInfo;

public class LogoutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.txt_logout_alert_title)
                .setMessage(R.string.txt_logout_alert_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.txt_logout_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent restartIntent =
                                new Intent(LogoutActivity.this, StartActivity.class);

                        AuthorizationInfo.clear(LogoutActivity.this);

                        startActivity(restartIntent);
                    }
                })
                .setNegativeButton(R.string.txt_logout_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.show();
    }
}
