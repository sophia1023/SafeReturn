package com.jh.safereturn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by HUNNY on 2015-12-07.
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service1 = new Intent(context, AlarmReceiver.class);
        context.startService(service1);
        context.stopService(service1);
    }
}