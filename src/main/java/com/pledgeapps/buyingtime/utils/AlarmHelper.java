package com.pledgeapps.buyingtime.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;

import com.pledgeapps.buyingtime.AlarmsActivity;
import com.pledgeapps.buyingtime.AlertActivity;

import java.util.Calendar;
import java.util.Date;

public class AlarmHelper extends BroadcastReceiver {

    private PendingIntent pi;
    private BroadcastReceiver br;
    private AlarmManager am;
    private String namespace = "com.pledgeapps.buyingtime";


    private static AlarmHelper current;

    public static AlarmHelper getCurrent() {
        if (current==null) current=new AlarmHelper();
        return current;
    };

    public AlarmHelper(){}

    public void setAlarm(Context context, Date alarmTime){
        this.am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //helper.pi = PendingIntent.getBroadcast(context, 0, new Intent(helper.namespace), PendingIntent.FLAG_UPDATE_CURRENT);
        this.pi = PendingIntent.getBroadcast(context, 0, new Intent(this.namespace), 0);
        this.am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000, this.pi);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "BUYINGTIMEALARM");
        wl.acquire(30000);
        Intent i = new Intent(context, AlertActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(i);

    }
}
