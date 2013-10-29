package com.pledgeapps.buyingtime.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Alarm {
    public int hour = 6;
    public int minute = 0;
    public boolean active = true;
    public int[] daysOfWeek = new int[0];
    public int graceMinutes = 9;
    public int centsPerMinute = 11;
    public int snoozeDuration = 9;
    public String guid;

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
        a.snoozeDuration = 9;
        a.centsPerMinute = 11;
        a.guid = java.util.UUID.randomUUID().toString();
        return a;
    }

    public static String getDisplayTime(int hour, int minute)
    {
        Boolean pm = false;
        int displayHour = hour;
        if (displayHour>11) { displayHour = displayHour-12; pm=true; }
        if (displayHour==0) displayHour = 12;
        String result = displayHour + ":" + String.format("%02d", minute);
        if (pm) result += " PM"; else result += " AM";
        return result;
    }



    public String getDisplayTime()
    {
        return Alarm.getDisplayTime(this.hour, this.minute);
    }

    public static String getDisplayDays(int[] daysOfWeek)
    {
        List<String> days = new ArrayList<String>();
        for (int i : daysOfWeek)
        {
            switch (i)
            {
                case 0: days.add("Su"); break;
                case 1: days.add("Mo"); break;
                case 2: days.add("Tu"); break;
                case 3: days.add("We"); break;
                case 4: days.add("Th"); break;
                case 5: days.add("Fr"); break;
                case 6: days.add("Sa"); break;
            }
        }
        return days.toString().replace("[", "").replace("]", "").replace(", ", ",");
    }

    public String getDisplayDays()
    {
        return Alarm.getDisplayDays(this.daysOfWeek);
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
                    this.nextNotificationTime = new Date(this.nextAlarmTime.getTime());
                } else {
                    cal.add(Calendar.DATE,1);
                }
            }
        }
    }

    public int getMinutesOverslept()
    {
        int result = (int)( (new Date().getTime() - this.nextAlarmTime.getTime()) / (1000 * 60) );
        if (result<0) result = 0;
        return result;
    }

    public double getCost()
    {
        double result = 0;
        int minutesOverslept = getMinutesOverslept();
        if (minutesOverslept > this.graceMinutes)
        {
            int minutesCharged = minutesOverslept - graceMinutes;
            result = minutesCharged * this.centsPerMinute / 100.0;
        }
        return result;
    }


    public boolean contains(final int[] array, final int key) {
        Arrays.sort(array);
        return Arrays.binarySearch(array, key) != -1;
    }

}
