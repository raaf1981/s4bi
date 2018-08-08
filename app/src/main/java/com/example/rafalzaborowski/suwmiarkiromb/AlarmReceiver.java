package com.example.rafalzaborowski.suwmiarkiromb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("akcja14");
        context.sendBroadcast(i);
        System.out.println("BR alarm");
    }
}
