package com.pledgeapps.buyingtime;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private Handler handler;

    static TimePickerFragment newInstance(Handler h, int hour, int minute)
    {
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        fragment.handler = h;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        final Calendar c = Calendar.getInstance();

        //The second input is a default value in case hour or minute are empty
        int hour = args.getInt("hour", c.get(Calendar.HOUR_OF_DAY));
        int minute = args.getInt("minute", c.get(Calendar.MINUTE));

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        Bundle b = new Bundle();
        b.putInt("hour", hourOfDay);
        b.putInt("minute", minute);
        Message m = new Message();
        m.setData(b);
        handler.sendMessage(m);
    }

}
