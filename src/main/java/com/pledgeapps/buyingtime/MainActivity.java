package com.pledgeapps.buyingtime;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pledgeapps.buyingtime.data.Alarms;

public class MainActivity extends ActionBarActivity {

    private static final int ACTIVITY_ALARMS=101;

    PendingIntent pi;
    BroadcastReceiver br;
    AlarmManager am;

    private void setup() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "BUYINGTIMEALARM");
                //PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "BUYINGTIMEALARM");
                wl.acquire(30000);


                Intent intent = new Intent(context, AlarmsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intent);

                //wl.release();
            }
        };

        registerReceiver(br, new IntentFilter(getString(R.string.namespace)) );
        pi = PendingIntent.getBroadcast(this, 0, new Intent(getString(R.string.namespace)), 0);
        am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new com.pledgeapps.buyingtime.MainFragment())
                    .commit();
            Alarms.load(getApplicationContext());
            setup();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_alarms:
                Intent i = new Intent(this, AlarmsActivity.class);
                startActivityForResult(i, ACTIVITY_ALARMS);
        }
        return super.onOptionsItemSelected(item);
    }



}
