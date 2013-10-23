package com.pledgeapps.buyingtime.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;

import com.pledgeapps.buyingtime.AlertActivity;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    private PendingIntent pi;
    private BroadcastReceiver br;
    private AlarmManager am;
    private String namespace = "com.pledgeapps.buyingtime";


    private static AlarmReceiver current;

    public static AlarmReceiver getCurrent() {
        if (current==null) current=new AlarmReceiver();
        return current;
    };

    public AlarmReceiver(){}

    public void setAlarm(Context context, Date alarmTime){
        this.am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //helper.pi = PendingIntent.getBroadcast(context, 0, new Intent(helper.namespace), PendingIntent.FLAG_UPDATE_CURRENT);

        Bundle bundle = new Bundle();
        bundle.putString("ALARM_GUID", "123");
        Intent intent = new Intent(this.namespace);
        intent.putExtras(bundle);

        this.pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        this.am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000, this.pi);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String guid = intent.getStringExtra("ALARM_GUID");

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "BUYINGTIMEALARM");
        wl.acquire(30000);

        // Close dialogs and window shade
        Intent closeDialogs = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeDialogs);


        Bundle bundle = new Bundle();
        bundle.putString("ALARM_GUID", guid);

        Intent i = new Intent(context, AlertActivity.class);
        i.putExtras(bundle);
        //i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        //i.setAction(Intent.ACTION_MAIN);
        //i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);

    }
}
