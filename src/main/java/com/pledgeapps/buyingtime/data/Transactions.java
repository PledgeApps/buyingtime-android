package com.pledgeapps.buyingtime.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Transactions extends ArrayList<Transaction> {

    private static Transactions current;

    public double getCurrentPledge()
    {
        double result = 0;
        for (Transaction t : this)
        {
            result += t.amount;
        }
        return result;
    }

    public double getTotalDonated()
    {
        double result = 0;
        for (Transaction t : this)
        {
            if (t.amount<0) result += t.amount;
        }
        return -result;
    }

    public static Transactions getCurrent() {
        if (current==null) current = new Transactions();
        return current;
    }


    public void save(Context context)
    {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("transactions", json);
        editor.commit();
    }

    public static void load(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString("transactions","");
        if (!json.equals(""))
        {
            Gson gson = new Gson();
            current = gson.fromJson(json, Transactions.class);
        }
    }


}
