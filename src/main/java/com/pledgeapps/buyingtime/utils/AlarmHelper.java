package com.pledgeapps.buyingtime.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Bundle;

import com.pledgeapps.buyingtime.AlertActivity;

import java.util.Date;

public class AlarmHelper {
    private static AlarmHelper current;
    public boolean isSounding = false;                      //Is the alarm current playing
    public boolean pendingAlarm = false;                    //Does the alarm need to start playing
    public Ringtone ringtone;
    private AlarmManager am;
    private PendingIntent pi;

    public static AlarmHelper getCurrent() {
        if (current==null) current=new AlarmHelper();
        return current;
    };

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

    public void showAlert(Context context, String guid)
    {
        this.pendingAlarm = true;
        Bundle bundle = new Bundle();
        bundle.putString("ALARM_GUID", guid);
        Intent i = new Intent(context, AlertActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtras(bundle);
        context.startActivity(i);
    }

}
