package com.pledgeapps.buyingtime.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Alarm {
    public int hour;
    public int minute;
    public boolean active;
    public int[] daysOfWeek;
    public int graceMinutes;
    public int centsPerMinute;

    public Date nextAlarmTime;              //This is the date/time the alarm is set for. (does not update when snoozed)
    public Date nextNotificationTime;       //This is the date/time it will next beep (updates when snoozed)


    public static Alarm createNewAlarm()
    {
        Alarm a = new Alarm();
        a.hour = 6;
        a.minute = 0;
        a.active = true;
        a.daysOfWeek = new int[]{1,2,3,4,5};
        a.graceMinutes = 9;
        a.centsPerMinute = 11;
        return a;
    }

    public String getDisplayTime()
    {
        Boolean pm = false;
        int displayHour = this.hour;
        if (displayHour>11) { displayHour = displayHour-12; pm=true; }
        if (displayHour==0) displayHour = 12;
        String result = displayHour + ":" + String.format("%02d", this.minute);
        if (pm) result += " PM"; else result += " AM";
        return result;
    }

    public String getDisplayDays()
    {
        List<String> days = new ArrayList<String>();
        for (int i : this.daysOfWeek)
        {
            switch (i)
            {
                case 0: days.add("Su"); break;
                case 1: days.add("Mo"); break;
                case 2: days.add("Th"); break;
                case 3: days.add("We"); break;
                case 4: days.add("Th"); break;
                case 5: days.add("Fr"); break;
                case 6: days.add("Sa"); break;
            }
        }
        return days.toString().replace("[", "").replace("]", "").replace(", ", ",");
    }

    public void updateNextAlarmTime()
    {
        //The alarm will not run.
        this.nextAlarmTime = null;
        this.nextNotificationTime = null;

        if (this.active && this.daysOfWeek.length>0)
        {


            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY,this.hour);
            cal.set(Calendar.MINUTE,this.minute);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);

            //If this time has already passed for today, start with tomorrow.
            if (!cal.after(Calendar.getInstance())) cal.add(Calendar.DATE,1);

            while (this.nextAlarmTime==null)
            {
                //Should the alarm fire on this day of the week?
                if (contains(this.daysOfWeek, cal.get(Calendar.DAY_OF_WEEK) - 1))
                {
                    this.nextAlarmTime = cal.getTime();
                    this.nextNotificationTime = this.nextAlarmTime;
                } else {
                    cal.add(Calendar.DATE,1);
                }
            }
        }

    }

    public boolean contains(final int[] array, final int key) {
        Arrays.sort(array);
        return Arrays.binarySearch(array, key) != -1;
    }

}
