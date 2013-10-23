package com.pledgeapps.buyingtime;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pledgeapps.buyingtime.data.Alarm;
import com.pledgeapps.buyingtime.data.Alarms;



public class AlarmListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;

    Context context=null;


    public AlarmListAdapter(Context context) { this.context = context;  mInflater = LayoutInflater.from(context); }
    public int getCount() { return Alarms.getCurrent().size(); }
    public Object getItem(int position) { return position; }
    public long getItemId(int position) { return position; }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        int currentAlarmIdx = 0; //Settings.getCurrent().getSchedule();





        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_alarm, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.alarmName);
            holder.alarmDays = (TextView) convertView.findViewById(R.id.alarmDays);
            holder.pledge = (TextView) convertView.findViewById(R.id.pledge);
            holder.activeToggle = (ToggleButton) convertView.findViewById(R.id.activeToggle);


            //holder.selectButton = (ImageView) convertView.findViewById(R.id.selectButton);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        Alarm a = Alarms.getCurrent().get(position);
        holder.name.setText(a.getDisplayTime());
        if (a.getDisplayDays()!="") holder.alarmDays.setText(" - " + a.getDisplayDays());
        if (a.centsPerMinute>0)
        {
            double dollars = a.centsPerMinute / 100.0;
            String displayPledge = "$" + String.format("%1.2f", dollars) + " / min.";
            if (a.graceMinutes>0) {
                displayPledge += " after " + Integer.toString(a.graceMinutes) + " min.";
            }
            holder.pledge.setText(displayPledge);
        } else {
            holder.pledge.setText("No pledge.");
        }
        holder.activeToggle.setChecked(a.active);





        final int currentPosition = position;


        holder.activeToggle.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {toggleAlarm(currentPosition, view);}});

        return convertView;
    }

    private void toggleAlarm(int idx, View view)
    {
        ToggleButton tmpActiveToggle = (ToggleButton) view.findViewById(R.id.activeToggle);
        Alarms.getCurrent().get(idx).active = tmpActiveToggle.isChecked();
        this.notifyDataSetChanged();
    }


    static class ViewHolder {
        TextView name;
        TextView alarmDays;
        TextView pledge;
        ToggleButton activeToggle;
    }
}
