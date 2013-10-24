package com.pledgeapps.buyingtime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.pledgeapps.buyingtime.data.Alarm;
import com.pledgeapps.buyingtime.utils.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AlertActivity extends Activity {


    TextView currentTime;
    Button snoozeButton;
    Button dismissButton;
    Handler refreshHandler;
    String previousDisplayTime = "";
    SimpleDateFormat formatter = new SimpleDateFormat("h:mma");
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        setContentView(R.layout.activity_alert);

        currentTime = (TextView) findViewById(R.id.currentTime);
        snoozeButton = (Button) findViewById(R.id.snoozeButton);
        dismissButton = (Button) findViewById(R.id.dismissButton);

        snoozeButton.setOnClickListener( new View.OnClickListener() {public void onClick(View view) {snooze();}} );
        dismissButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });

        refreshHandler= new Handler();
        refreshHandler.postDelayed(refreshRunnable, 1000);
        updateScreen(true);
        soundAlarm();


        // Register to get the alarm killed intent.
        //registerReceiver(mReceiver, new IntentFilter(Alarms.ALARM_KILLED));
    }


    @Override
    protected void onResume() {
        super.onResume();
        soundAlarm();
    }


    private void silenceAlarm()
    {
        AlarmReceiver.getCurrent().ringtone.stop();
        AlarmReceiver.getCurrent().isSounding = false;
        //vibrator.cancel();
    }

    private void soundAlarm()
    {
        //vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //vibrator.vibrate(3600 * 1000); //For 1 hour unless dismissed.


        AlarmReceiver ar = AlarmReceiver.getCurrent();

        if (!ar.isSounding && ar.pendingAlarm)
        {
            snoozeButton.setText("Snooze");
            snoozeButton.setEnabled(true);

            if (AlarmReceiver.getCurrent().ringtone==null)
            {
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if(alert == null){
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    if(alert == null) alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
                AlarmReceiver.getCurrent().ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
            }
            AlarmReceiver.getCurrent().ringtone.play();
            AlarmReceiver.getCurrent().isSounding = true;
            AlarmReceiver.getCurrent().pendingAlarm = false;
        }
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
        String displayTime = formatter.format(new Date()).toLowerCase().replace("m", "");
        if (!displayTime.equals(previousDisplayTime))
        {
            currentTime.setText(displayTime);
            previousDisplayTime=displayTime;
        }
    }


/*
    public void onPause()
    {
        //The activity will pause and resume when triggered while the screen is locked.
        //Check if the response time was too quick for a human and don't snooze.
        //The alarm can still be dismissed or snoozed in this timeframe by pushing one of those
        //buttons
        if ( (new Date().getTime() - launchTime.getTime()) > 300 )
        {
            new Thread(new Runnable() {
                public void run() {
                    snooze();
                }
            }).start();
        }
        super.onPause();
    }
*/



    private void snooze()
    {
        silenceAlarm();

        Date nextAlarmTime = new Date();
        nextAlarmTime.setTime( nextAlarmTime.getTime() + 1 * 30 * 1000 ); //9 minutes
        AlarmReceiver.getCurrent().setAlarm(getApplicationContext(), nextAlarmTime);
        snoozeButton.setText("Snoozing...");
        snoozeButton.setEnabled(false);

        //finish();
    }

    private void dismiss()
    {
        silenceAlarm();
        finish();

    }


}
