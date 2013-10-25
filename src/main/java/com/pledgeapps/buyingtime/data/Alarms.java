package com.pledgeapps.buyingtime.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;


public class Alarms extends ArrayList<Alarm> {

    private static Alarms current;




    public static Alarms getCurrent() {
        if (current==null) current=new Alarms();
        return current;
    };

    public void updateNextAlarmTime()
    {
        for (Alarm a : this)
        {
            a.updateNextAlarmTime();
        }
    }

    public Alarm getNextAlarm()
    {
        Alarm result = null;
        for (Alarm a : this)
        {
            if (a.nextAlarmTime!=null)
            {
                if (result==null || a.nextAlarmTime.before(result.nextAlarmTime)) result = a;
            }
        }
        return result;
    }

    public void save(Context context)
    {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("alarms", json);
        editor.commit();
    }

    public static void load(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString("alarms","");
        if (!json.equals(""))
        {
            Gson gson = new Gson();
            current = gson.fromJson(json, Alarms.class);
        }
    }

    public Alarm getByGuid(String guid)
    {
        for (Alarm a : this)
        {
            if (a.guid==guid) return a;
        }
        return null;
    }


}
