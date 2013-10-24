package com.pledgeapps.buyingtime.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.PowerManager;

import com.pledgeapps.buyingtime.AlertActivity;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {


    private BroadcastReceiver br;




    public AlarmReceiver(){}



    @Override
    public void onReceive(Context context, Intent intent) {

        String guid = intent.getStringExtra("ALARM_GUID");

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "BUYINGTIMEALARM");
        wl.acquire(30000);

        // Close dialogs and window shade
        Intent closeDialogs = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeDialogs);
        AlarmHelper.getCurrent().showAlert(context, guid);

    }



}
