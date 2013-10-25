package com.pledgeapps.buyingtime.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Bundle;

import com.pledgeapps.buyingtime.AlertActivity;
import com.pledgeapps.buyingtime.data.Alarm;
import com.pledgeapps.buyingtime.data.Alarms;

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


    private void disableAllAlarms(Context context)
    {
        this.am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //Remove any existing alarms
        if (this.pi!=null) { am.cancel(pi); }
    }

    public void setAlarm(Context context, Alarm alarm)
    {
        disableAllAlarms(context);
        if (alarm.nextNotificationTime==null) return;

        //Create the new alarm.
        Bundle bundle = new Bundle();
        bundle.putString("ALARM_GUID", alarm.guid );
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtras(bundle);
        this.pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        this.am.set(AlarmManager.RTC_WAKEUP, alarm.nextNotificationTime.getTime(), this.pi);
    }

    public void showAlert(Context context, Alarm alarm)
    {
        this.pendingAlarm = true;
        Bundle bundle = new Bundle();
        bundle.putString("ALARM_GUID", alarm.guid);
        Intent i = new Intent(context, AlertActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtras(bundle);
        context.startActivity(i);
    }

    public void updateAlarms(Context context)
    {
        Alarms alarms = Alarms.getCurrent();
        alarms.updateNextAlarmTime();
        alarms.save(context);
        Alarm nextAlarm = alarms.getNextAlarm();
        this.disableAllAlarms(context);
        if (nextAlarm!=null && nextAlarm.nextNotificationTime!=null) this.setAlarm(context, nextAlarm);
    }

}
