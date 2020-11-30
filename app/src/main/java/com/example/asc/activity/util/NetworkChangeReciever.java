package com.example.asc.activity.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isNetworkAvailable = false, isWifiAvailable = false, isMobileAvailable = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            isNetworkAvailable = networkInfo.isAvailable();
            isWifiAvailable = networkInfo.isAvailable() && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
            isMobileAvailable = networkInfo.isAvailable() && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
        }

        if (isNetworkAvailable) {
            if (isWifiAvailable) {
                Toast.makeText(context, "当前网络状态：Wifi可用", Toast.LENGTH_SHORT).show();
            } else if (isMobileAvailable) {
                Toast.makeText(context, "当前网络状态：移动数据可用", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "当前网络状态：网络不可用", Toast.LENGTH_SHORT).show();
        }
    }
}
