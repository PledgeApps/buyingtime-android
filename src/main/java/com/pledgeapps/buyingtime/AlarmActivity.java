package com.pledgeapps.buyingtime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.pledgeapps.buyingtime.data.Alarm;
import com.pledgeapps.buyingtime.data.Alarms;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends ActionBarActivity {

    EditText graceMinutesText;
    EditText centsPerMinuteText;
    Button alarmTimeButton;
    Button daysButton;
    Button saveButton;
    Button deleteButton;
    ToggleButton activeToggle;

    String[] daysOfTheWeek = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    int[] selectedDays = new int[0];
    int selectedHour = 0;
    int selectedMinute = 0;
    int alarmIndex = 0;
    Alarm alarm;


    Handler timeHandler = new Handler() {
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            selectedHour = b.getInt("hour");
            selectedMinute = b.getInt("minute");
            alarmTimeButton.setText(Alarm.getDisplayTime(selectedHour,selectedMinute));
        }
    };

    Handler daysHandler = new Handler() {
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            ArrayList<Integer> days = (ArrayList<Integer>) b.get("selectedIndexes");
            selectedDays = new int[days.size()];
            for (int i=0;i<days.size();i++) selectedDays[i] = days.get(i);
            daysButton.setText(Alarm.getDisplayDays(selectedDays));
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Bundle extras = getIntent().getExtras();
        if (extras != null) alarmIndex = extras.getInt("ALARM_INDEX");
        if (alarmIndex==-1) alarm = Alarm.createNewAlarm(); else alarm = Alarms.getCurrent().get(alarmIndex);

        activeToggle = (ToggleButton) findViewById(R.id.activeToggle);
        graceMinutesText = (EditText) findViewById(R.id.graceMinutesText);
        centsPerMinuteText = (EditText) findViewById(R.id.centsPerMinuteText);
        alarmTimeButton = (Button) findViewById(R.id.alarmTimeButton);
        daysButton = (Button) findViewById(R.id.daysButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        alarmTimeButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {setTime();}});
        daysButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {setDays();}});
        saveButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {save();}});
        deleteButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {delete();}});




        populateFields();


        //alarmList = (ListView) findViewById(R.id.alarmList);
    }

    private void populateFields()
    {
        this.selectedHour = alarm.hour;
        this.selectedMinute = alarm.minute;
        this.selectedDays = alarm.daysOfWeek;

        activeToggle.setChecked(alarm.active);
        centsPerMinuteText.setText(Integer.toString(alarm.centsPerMinute));
        graceMinutesText.setText(Integer.toString(alarm.graceMinutes));
        alarmTimeButton.setText(alarm.getDisplayTime());
        daysButton.setText(alarm.getDisplayDays());

    }

    private void setTime()
    {
        DialogFragment timeFragment = TimePickerFragment.newInstance(timeHandler, selectedHour, selectedMinute);
        timeFragment.show(this.getSupportFragmentManager(), "timePicker");
    }

    private void setDays()
    {
        ArrayList<Integer> selectedIndexes = new ArrayList<Integer>();
        for (int day : selectedDays) selectedIndexes.add(day);
        DialogFragment daysFragment = DaysOfWeekFragment.newInstance(daysHandler, selectedIndexes);
        daysFragment.show(this.getSupportFragmentManager(), "dayPicker");
    }

    private void delete()
    {
        if (alarmIndex>-1) Alarms.getCurrent().remove(alarmIndex);
        this.finish();
    }

    private void save()
    {
        alarm.minute = selectedMinute;
        alarm.hour = selectedHour;

        alarm.graceMinutes = Integer.parseInt(graceMinutesText.getText().toString());
        alarm.centsPerMinute = Integer.parseInt(centsPerMinuteText.getText().toString());
        alarm.active = activeToggle.isChecked();
        alarm.daysOfWeek = selectedDays;
        if (alarmIndex==-1) Alarms.getCurrent().add(alarm);

        this.finish();
    }


}
