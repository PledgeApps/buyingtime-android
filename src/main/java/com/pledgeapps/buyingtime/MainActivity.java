package com.pledgeapps.buyingtime;


import android.app.AlarmManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.pledgeapps.buyingtime.data.Alarms;
import com.pledgeapps.buyingtime.utils.AlarmReceiver;

public class MainActivity extends ActionBarActivity {

    private static final int ACTIVITY_ALARMS=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new com.pledgeapps.buyingtime.MainFragment())
                    .commit();
            Alarms.load(getApplicationContext());
           // registerReceiver(AlarmReceiver.getCurrent(), new IntentFilter(getString(R.string.namespace)));
        }
        checkAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAlarm();
    }

    private void checkAlarm()
    {
        if (AlarmReceiver.getCurrent().isSounding) AlarmReceiver.getCurrent().showAlert(this, "");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_alarms:
                Intent i = new Intent(this, AlarmsActivity.class);
                startActivityForResult(i, ACTIVITY_ALARMS);
        }
        return super.onOptionsItemSelected(item);
    }



}
