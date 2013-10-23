package com.pledgeapps.buyingtime;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;



import java.text.SimpleDateFormat;
import java.util.Date;


public class AlertActivity extends Activity {


    TextView currentTime;
    Button snoozeButton;
    Button dismissButton;
    Handler refreshHandler;
    String previousDisplayTime = "";
    SimpleDateFormat formatter = new SimpleDateFormat("h:mma");
    MediaPlayer mp;
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
        dismissButton.setOnClickListener( new View.OnClickListener() {public void onClick(View view) {dismiss();}} );


        refreshHandler= new Handler();
        refreshHandler.postDelayed(refreshRunnable, 1000);
        updateScreen(true);
        soundAlarm();


        // Register to get the alarm killed intent.
        //registerReceiver(mReceiver, new IntentFilter(Alarms.ALARM_KILLED));


    }


    private void silenceAlarm()
    {
        mp.stop();
        vibrator.cancel();
    }

    private void soundAlarm()
    {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(3600 * 1000); //For 1 hour unless dismissed.
        //Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(),
          //      RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        //if (ringtone != null) {
            //ringtone.play();



        try {
            mp = new MediaPlayer();
            mp.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mp.setAudioStreamType(AudioManager.STREAM_RING);
                mp.setLooping(true);
                mp.prepare();
                mp.start();
            }
        } catch(Exception e) {
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


    @Override
    public void onPause()
    {
        new Thread(new Runnable() {
            public void run() {
                silenceAlarm();
                //snooze();
            }
        }).start();
        super.onPause();
    }



    private void snooze()
    {
        silenceAlarm();
        //finish();
    }

    private void dismiss()
    {
        silenceAlarm();
        finish();

    }


}
