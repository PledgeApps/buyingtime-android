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
import com.pledgeapps.buyingtime.data.Alarms;
import com.pledgeapps.buyingtime.data.Transaction;
import com.pledgeapps.buyingtime.data.Transactions;
import com.pledgeapps.buyingtime.utils.AlarmHelper;
import com.pledgeapps.buyingtime.utils.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AlertActivity extends Activity {


    TextView currentHour;
    TextView currentMinute;
    TextView currentPeriod;
    TextView currentDate;
    TextView alarmTimeText;
    TextView oversleptText;
    TextView chargeText;

    Button snoozeButton;
    Button dismissButton;
    Handler refreshHandler;
    String previousDisplayTime = "";
    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
    SimpleDateFormat hourFormat = new SimpleDateFormat("h");
    SimpleDateFormat minuteFormat = new SimpleDateFormat(":mm");
    SimpleDateFormat periodFormat = new SimpleDateFormat("a");
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d");
    Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alert);

        loadAlarm();

        currentHour = (TextView) findViewById(R.id.currentHour);
        currentMinute = (TextView) findViewById(R.id.currentMinute);
        currentPeriod = (TextView) findViewById(R.id.currentPeriod);
        currentDate = (TextView) findViewById(R.id.currentDate);
        alarmTimeText = (TextView) findViewById(R.id.alarmTimeText);
        oversleptText = (TextView) findViewById(R.id.oversleptText);
        chargeText = (TextView) findViewById(R.id.chargeText);
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
        loadAlarm();
        soundAlarm();
    }


    private void silenceAlarm()
    {
        AlarmHelper.getCurrent().ringtone.stop();
        AlarmHelper.getCurrent().vibrator.cancel();
        AlarmHelper.getCurrent().isSounding = false;
        //vibrator.cancel();
    }

    private void soundAlarm()
    {

        AlarmHelper helper = AlarmHelper.getCurrent();

        if (!helper.isSounding && helper.pendingAlarm)
        {
            snoozeButton.setText("Snooze");
            snoozeButton.setEnabled(true);

            if (helper.ringtone==null)
            {
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if(alert == null){
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    if(alert == null) alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
                AlarmHelper.getCurrent().ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
            }
            if (helper.vibrator==null)
            {
                helper.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            }
            helper.ringtone.play();
            helper.vibrator.vibrate(3600 * 1000); //For 1 hour unless dismissed.
            helper.isSounding = true;
            helper.pendingAlarm = false;
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
        String displayTime = timeFormat.format(new Date()).toLowerCase().replace("m", "");
        if (!displayTime.equals(previousDisplayTime))
        {
            Date displayDate = new Date();
            currentHour.setText( hourFormat.format(displayDate) );
            currentMinute.setText( minuteFormat.format(displayDate) );
            currentPeriod.setText( periodFormat.format(displayDate) );
            currentDate.setText( dateFormat.format(displayDate) );

            alarmTimeText.setText(timeFormat.format(alarm.nextAlarmTime).toLowerCase());
            oversleptText.setText(Integer.toString(alarm.getMinutesOverslept()) + " min" );
            String displayPledge = "$" + String.format("%1.2f", alarm.getCost());
            chargeText.setText(displayPledge );
            previousDisplayTime=displayTime;
        }
    }




    private void snooze()
    {
        silenceAlarm();

        //Date nextAlarmTime = new Date();
        alarm.nextNotificationTime.setTime( alarm.nextNotificationTime.getTime() + 9 * 60 * 1000 ); //9 minutes
        AlarmHelper.getCurrent().setAlarm(getApplicationContext(), alarm);
        snoozeButton.setText("Snoozing...");
        snoozeButton.setEnabled(false);

        //finish();
    }

    private void dismiss()
    {
        silenceAlarm();
        if (alarm.getCost()>0)
        {
            Transaction t = new Transaction();
            t.amount = alarm.getCost();
            t.date = new Date();
            Transactions.getCurrent().add(t);
            Transactions.getCurrent().save(this);
        }
        AlarmHelper.getCurrent().updateAlarms(getApplicationContext());
        finish();
    }

    private void loadAlarm()
    {
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String guid = extras.getString("ALARM_GUID");
            this.alarm = Alarms.getCurrent().getByGuid(guid);
        }
    }


}
