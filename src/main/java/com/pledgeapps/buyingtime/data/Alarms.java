package com.pledgeapps.buyingtime.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;


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



}
