package com.example.tarasantoshchuk.vimeoapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.util.AuthorizationInfo;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        /**
         * check if internet connection is available
         */
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();


        if(!isConnected) {
            /**
             * create and show alert dialog, then finish application
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setTitle(getString(R.string.txt_connection_fail))
                    .setMessage(getString(R.string.txt_check_connection))
                    .setNeutralButton(getString(R.string.txt_close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        } else {



            if(AuthorizationInfo.hasAccessToken()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getAuthorizationAddress()));
                startActivity(intent);
            } else {
                /**
                 * go to view with logged user profile/videos
                 */
            }

        }
    }

    private String getAuthorizationAddress() {
        return new StringBuilder()
                .append(getString(R.string.authorization_address))
                .append("?response_type=code")
                .append("&client_id=")
                .append(getString(R.string.client_id))
                .append("&redirect_uri=")
                .append(getString(R.string.callback_url))
                .append("&state=1")
                .toString();
    }
}
