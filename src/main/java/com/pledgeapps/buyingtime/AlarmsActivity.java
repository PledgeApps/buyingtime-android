package com.pledgeapps.buyingtime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;

import com.pledgeapps.buyingtime.data.Alarms;
import com.pledgeapps.buyingtime.utils.AlarmHelper;
import com.pledgeapps.buyingtime.utils.AlarmReceiver;

import java.util.Date;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class AlarmsActivity extends Activity {
    ListView alarmList;
    AlarmListAdapter alarmListAdapter;
    private static final int ACTIVITY_EDITALARM=110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarms);
        alarmList = (ListView) findViewById(R.id.alarmList);


        alarmList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                selectAlarm(position);
            }
        });

        alarmListAdapter = new AlarmListAdapter(this);
        alarmList.setAdapter(alarmListAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        alarmListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause()
    {
        new Thread(new Runnable() {
            public void run() {
                saveData();
            }
        }).start();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.alarms, menu);
        return true;
    }

    //onPause
    private void saveData()
    {
        Alarms alarms = Alarms.getCurrent();
        alarms.updateNextAlarmTime();
        alarms.save(getApplicationContext());
        Date nextAlarmTime = alarms.getNextAlarmTime();
        if (nextAlarmTime!=null) AlarmHelper.getCurrent().setAlarm(getApplicationContext(), nextAlarmTime);
    }

    private void selectAlarm(int alarmIndex)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("ALARM_INDEX", alarmIndex);
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ACTIVITY_EDITALARM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_alarm:
                selectAlarm(-1);
        }
        return super.onOptionsItemSelected(item);
    }

}
