package com.pledgeapps.buyingtime.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.pledgeapps.buyingtime.AlarmsActivity;
import com.pledgeapps.buyingtime.MainActivity;
import com.pledgeapps.buyingtime.R;
import com.pledgeapps.buyingtime.data.Alarms;
import com.pledgeapps.buyingtime.utils.AlarmReceiver;

public class BootReceiver extends BroadcastReceiver  {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            /*
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);*/

            Alarms.load(context);
            Alarms alarms = Alarms.getCurrent();
            alarms.updateNextAlarmTime();
            AlarmHelper.getCurrent().setAlarm(context, alarms.getNextAlarmTime());



            //registerReceiver(AlarmReceiver.getCurrent(), new IntentFilter(getString(R.string.namespace)));
        }
    }
}