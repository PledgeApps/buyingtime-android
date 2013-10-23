package com.pledgeapps.buyingtime;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.pledgeapps.buyingtime.data.Alarm;
import com.pledgeapps.buyingtime.data.Alarms;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends Activity {

    EditText graceMinutesText;
    EditText centsPerMinuteText;
    Spinner hourList;
    Spinner minuteList;
    RadioGroup periodRadio;
    CheckBox sundayCheck;
    CheckBox mondayCheck;
    CheckBox tuesdayCheck;
    CheckBox wednesdayCheck;
    CheckBox thursdayCheck;
    CheckBox fridayCheck;
    CheckBox saturdayCheck;
    Button saveButton;
    Button deleteButton;
    ToggleButton activeToggle;

    int selectedHourIndex = 0;
    int selectedMinuteIndex = 0;
    int alarmIndex = 0;
    Alarm alarm;
    String[] hours = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
    String[] minutes = new String[] {
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"
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
        hourList = (Spinner) findViewById(R.id.hourList);
        minuteList = (Spinner) findViewById(R.id.minuteList);
        periodRadio = (RadioGroup) findViewById(R.id.periodRadio);
        sundayCheck = (CheckBox) findViewById(R.id.sundayCheck);
        mondayCheck = (CheckBox) findViewById(R.id.mondayCheck);
        tuesdayCheck = (CheckBox) findViewById(R.id.tuesdayCheck);
        wednesdayCheck = (CheckBox) findViewById(R.id.wednesdayCheck);
        thursdayCheck = (CheckBox) findViewById(R.id.thursdayCheck);
        fridayCheck = (CheckBox) findViewById(R.id.fridayCheck);
        saturdayCheck = (CheckBox) findViewById(R.id.saturdayCheck);
        saveButton = (Button) findViewById(R.id.saveButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        saveButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {save();}});
        deleteButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {delete();}});

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, hours);
        hourList.setAdapter(adapter);
        hourList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { selectedHourIndex=pos; }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, minutes);
        minuteList.setAdapter(adapter);
        minuteList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { selectedMinuteIndex=pos; }
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        populateFields();


        //alarmList = (ListView) findViewById(R.id.alarmList);
    }

    private void populateFields()
    {
        activeToggle.setChecked(alarm.active);
        centsPerMinuteText.setText(Integer.toString(alarm.centsPerMinute));
        graceMinutesText.setText(Integer.toString(alarm.graceMinutes));
        periodRadio.check(R.id.periodAM);
        selectedHourIndex = alarm.hour;
        if (selectedHourIndex>11)
        {
            selectedHourIndex = selectedHourIndex - 12;
            periodRadio.check(R.id.periodPM);
        }
        selectedHourIndex = selectedHourIndex - 1;
        hourList.setSelection(selectedHourIndex);
        selectedMinuteIndex = alarm.minute;
        minuteList.setSelection(selectedMinuteIndex);
        for (int i : alarm.daysOfWeek )
        {
            switch (i){
                case 0: sundayCheck.setChecked(true);break;
                case 1: mondayCheck.setChecked(true);break;
                case 2: tuesdayCheck.setChecked(true);break;
                case 3: wednesdayCheck.setChecked(true);break;
                case 4: thursdayCheck.setChecked(true);break;
                case 5: fridayCheck.setChecked(true);break;
                case 6: saturdayCheck.setChecked(true);break;
            }
        }
    }

    private void delete()
    {
        if (alarmIndex>-1) Alarms.getCurrent().remove(alarmIndex);
        this.finish();
    }

    private void save()
    {
        int hour = selectedHourIndex + 1;
        if (hour==12) hour = 0;
        if (periodRadio.getCheckedRadioButtonId() == R.id.periodPM) hour += 12;
        alarm.hour = hour;
        alarm.minute = selectedMinuteIndex;
        alarm.graceMinutes = Integer.parseInt(graceMinutesText.getText().toString());
        alarm.centsPerMinute = Integer.parseInt(centsPerMinuteText.getText().toString());
        alarm.active = activeToggle.isChecked();

        List<Integer> days = new ArrayList<Integer>();
        if (sundayCheck.isChecked()) days.add(0);
        if (mondayCheck.isChecked()) days.add(1);
        if (tuesdayCheck.isChecked()) days.add(2);
        if (wednesdayCheck.isChecked()) days.add(3);
        if (thursdayCheck.isChecked()) days.add(4);
        if (fridayCheck.isChecked()) days.add(5);
        if (saturdayCheck.isChecked()) days.add(6);
        alarm.daysOfWeek = new int[days.size()];
        for (int i =0; i<days.size(); i++) alarm.daysOfWeek[i] = days.get(i).intValue();

        if (alarmIndex==-1) Alarms.getCurrent().add(alarm);

        this.finish();
    }

}
