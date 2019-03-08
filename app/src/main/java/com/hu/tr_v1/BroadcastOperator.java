package com.hu.tr_v1;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastOperator {
    private static LocalBroadcastManager localBroadcastManager;
    private static LocalReceiver localReceiver;
    private static final String renewData = "com.hu.tr_v1.RENEW_DATA";

    public void initBroadcastManager(LocalBroadcastManager localBroadcastManager){
        this.localBroadcastManager = localBroadcastManager;
    }

    public void bindForRenewingData(LocalReceiver localReceiver){
        IntentFilter intentFilter = new IntentFilter(renewData);
        this.localReceiver = localReceiver;
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    public static void broadcastRenewing(){
        Intent intent = new Intent(renewData);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void onDestory(){
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

}
