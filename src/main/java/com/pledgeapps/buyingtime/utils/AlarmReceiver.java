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

    private PendingIntent pi;
    private BroadcastReceiver br;
    private AlarmManager am;
    private String namespace = "com.pledgeapps.buyingtime";
    public boolean isSounding = false;                      //Is the alarm current playing
    public boolean pendingAlarm = false;                    //Does the alarm need to start playing
    public Ringtone ringtone;

    private static AlarmReceiver current;

    public static AlarmReceiver getCurrent() {
        if (current==null) current=new AlarmReceiver();
        return current;
    };

    public AlarmReceiver(){}

    public void setAlarm(Context context, Date alarmTime)
    {
        this.am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //Remove any existing alarms
        if (this.pi!=null) { am.cancel(pi); }
        if (alarmTime==null) return;

        //Create the new alarm.
        Bundle bundle = new Bundle();
        bundle.putString("ALARM_GUID", "123");
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtras(bundle);
        this.pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        this.am.set(AlarmManager.RTC_WAKEUP, alarmTime.getTime(), this.pi);
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
        showAlert(context, guid);

    }

    public void showAlert(Context context, String guid)
    {
        current.pendingAlarm = true;                //The instance created here is not the shared "current" instance.
        Bundle bundle = new Bundle();
        bundle.putString("ALARM_GUID", guid);
        Intent i = new Intent(context, AlertActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtras(bundle);
        context.startActivity(i);
    }

}
