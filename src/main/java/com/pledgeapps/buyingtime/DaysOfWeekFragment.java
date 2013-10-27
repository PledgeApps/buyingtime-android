package com.pledgeapps.buyingtime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class DaysOfWeekFragment extends DialogFragment {

    String[] daysOfTheWeek = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    ArrayList<Integer> selectedIndexes;

    private Handler handler;

    static DaysOfWeekFragment newInstance(Handler h, ArrayList<Integer> selectedIndexes)
    {
        Bundle args = new Bundle();
        args.putIntegerArrayList("selectedIndexes", selectedIndexes);
        DaysOfWeekFragment fragment = new DaysOfWeekFragment();
        fragment.setArguments(args);
        fragment.handler = h;
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        selectedIndexes = args.getIntegerArrayList("selectedIndexes");

        boolean[] checkedItems = new boolean[]{false, false, false, false, false, false, false};
        for (int i=0;i<checkedItems.length;i++)
        {
            if (selectedIndexes.contains(i)) checkedItems[i]=true;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Days of the Week").setMultiChoiceItems(daysOfTheWeek, checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) selectedIndexes.add(which);
                        else if (selectedIndexes.contains(which)) selectedIndexes.remove(Integer.valueOf(which));
                    }
                });

        builder.setPositiveButton(R.string.action_set, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Bundle b = new Bundle();
                b.putIntegerArrayList("selectedIndexes", selectedIndexes);
                Message m = new Message();
                m.setData(b);
                handler.sendMessage(m);
            }
        });

        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { }
        });

        return builder.create();
    }
}
