package com.pledgeapps.buyingtime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pledgeapps.buyingtime.R;
import com.pledgeapps.buyingtime.data.Alarm;
import com.pledgeapps.buyingtime.data.Alarms;
import com.pledgeapps.buyingtime.data.Transaction;
import com.pledgeapps.buyingtime.data.Transactions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    String previousDisplayTime = "";
    Handler refreshHandler;
    TextView currentHour;
    TextView currentMinute;
    TextView currentPeriod;
    TextView currentDate;
    TextView alarmTime;
    TextView alarmRemaining;
    TextView currentPledge;
    TextView totalDonated;

    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
    SimpleDateFormat hourFormat = new SimpleDateFormat("h");
    SimpleDateFormat minuteFormat = new SimpleDateFormat(":mm");
    SimpleDateFormat periodFormat = new SimpleDateFormat("a");
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d");




    public MainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        refreshHandler= new Handler();
        refreshHandler.postDelayed(refreshRunnable, 1000);


        currentHour = (TextView) rootView.findViewById(R.id.currentHour);
        currentMinute = (TextView) rootView.findViewById(R.id.currentMinute);
        currentPeriod = (TextView) rootView.findViewById(R.id.currentPeriod);
        currentDate = (TextView) rootView.findViewById(R.id.currentDate);
        alarmTime = (TextView) rootView.findViewById(R.id.alarmTime);
        alarmRemaining = (TextView) rootView.findViewById(R.id.alarmRemaining);
        currentPledge = (TextView) rootView.findViewById(R.id.currentPledge);
        totalDonated = (TextView) rootView.findViewById(R.id.totalDonated);

        updateScreen(true);
        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateScreen(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateScreen(true);
    }


    private Runnable refreshRunnable = new Runnable() {
        public void run() {
            try {
                updateScreen(false);
            } catch (Exception e) {}
            refreshHandler.postDelayed(this, 1000);
        }
    };


    public void updateScreen(boolean forceRefresh)
    {
        Alarm a = Alarms.getCurrent().getNextAlarm();

        String displayTime = timeFormat.format(new Date()).toLowerCase().replace("m", "");
        String displayAlarmTime = "";
        if (a!=null) displayAlarmTime = a.getDisplayTime();

        if (!displayTime.equals(previousDisplayTime) || !alarmTime.getText().equals(displayAlarmTime) || forceRefresh)
        {
            Date displayDate = new Date();
            currentHour.setText( hourFormat.format(displayDate) );
            currentMinute.setText( minuteFormat.format(displayDate) );
            currentPeriod.setText( periodFormat.format(displayDate) );
            currentDate.setText( dateFormat.format(displayDate) );


            if (a==null)
            {
                alarmTime.setText("");
                alarmRemaining.setText("");
            } else {
                long seconds = (long) (a.nextAlarmTime.getTime() - new Date().getTime())/1000;
                int hours = (int)seconds / 3600;
                int minutes = (int) (seconds-hours*3600) / 60;
                if (hours<24)
                {
                    alarmTime.setText("Alarm: " + a.getDisplayTime());
                    alarmRemaining.setText("in " + Integer.toString(hours) + "h " + Integer.toString(minutes) + "m" );
                } else {
                    alarmTime.setText("");
                    alarmRemaining.setText("");
                }
            }

            currentPledge.setText("Current Pledge: " + "$" + String.format("%1.2f", Transactions.getCurrent().getCurrentPledge()));
            totalDonated.setText("Total Donated: " + "$" + String.format("%1.2f", Transactions.getCurrent().getTotalDonated()));

            previousDisplayTime=displayTime;
        }
    }

}