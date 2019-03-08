package com.hu.tr_v1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class LocalReceiver extends BroadcastReceiver {

    private MainActivity mainActivity;
    private final String TAG = "LocalReceiver";
    public LocalReceiver(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "received local broadcast", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Broadcast received, preparing to renew data.");
        mainActivity.renewData();
    }
}
