package com.intimealarm.conor.intime_app.logic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.intimealarm.conor.intime_app.MainActivity;
import com.intimealarm.conor.intime_app.utilities.Constants;
import com.intimealarm.conor.intime_app.utilities.Helper;
import com.intimealarm.conor.intime_app.models.Alarm;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @Author Conor Keenan
 * Student No: x13343806
 * Created on 24/01/2017.
 */

public class AlarmController {

    // Variables
    private Context context;
    private Helper help;
    private AlarmManager aManager;

    // Constructor
    public AlarmController(Context context) {
        this.context = context;
        this.help = new Helper();
        aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    }

    // Set Alarm - called to set a new alarm after request is made to the web app.
    public void setAlarm(Alarm a, long arrival_time, int type) {
        // check if request was successful
        if (arrival_time == -1){
            Log.d(Constants.TAG_ALARM_CONTROLLER, "setAlarm: arrival = -1");
        } else{

            Calendar alarmTime = help.getTimeFromString(a.getTime());
            Calendar originalTime = help.getTimeFromString(a.getDueTime());
            Calendar arrivalTime = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            arrivalTime.setTimeInMillis(arrival_time);

            // Calculate activation time
            if (arrivalTime.getTimeInMillis() > originalTime.getTimeInMillis()){
                long timeDiff = arrivalTime.getTimeInMillis() - originalTime.getTimeInMillis();
                long newTime = alarmTime.getTimeInMillis() - timeDiff;
                if(newTime > now.getTimeInMillis()) {
                    alarmTime.setTimeInMillis(newTime);
                    a.setTime(help.calToShortString(alarmTime));
                }
            }

            Log.d(Constants.TAG_ALARM_CONTROLLER, "setAlarm: Due Time: "+ help.timePrintOut(originalTime));
            Log.d(Constants.TAG_ALARM_CONTROLLER, "setAlarm: Arrival Time: "+ help.timePrintOut(arrivalTime));
            Log.d(Constants.TAG_ALARM_CONTROLLER, "setAlarm: Alarm Time: "+ help.timePrintOut(alarmTime));

        }

        // setting the alarm
        if (type == Constants.SMART_SINGLE_ALARM){
            singleAlarm(a, Constants.SINGLE_ALARM);
        }
        else if (type == Constants.SMART_REPEATING_ALARM){
            singleAlarm(a, Constants.SINGLE_NO_DISABLE);
        }

    }

    // Set Alarm - Called to set alarm based of an Alarm Object
    public void setAlarm(Alarm a){
        int type = checkAlarm(a);
        Log.d(Constants.TAG_ALARM_CONTROLLER, "setAlarm Type: "+ type);

        if(a.getActive()){
            if(type == Constants.REPEATING_ALARM || type == Constants.SMART_REPEATING_ALARM){
                repeatingAlarm(a, type);
            }else if(type == Constants.SINGLE_ALARM|| type == Constants.SMART_SINGLE_ALARM){
                singleAlarm(a, type);
            }
        }else {
            stopAlarm(a.getId());
        }

    }

    // Set a Stats Alarm
    public void setStatsAlarm(Alarm a){
        singleAlarm(a, Constants.STATS_ALARM);
    }

    // Check what type of alarm it is ie single , repeating, smart single or smart repeating
    private int checkAlarm(Alarm a) {

        Boolean isRepeating = false;

        for(int i = 0; i < a.getDays().length; i++){
            if(a.getDays()[i] == 1){
                isRepeating = true;
                break;
            }
        }

        Boolean isSmart = false;
        if (a.getTo() != -1 && a.getFrom() != -1 ){
            isSmart = true;
        }

        if (isSmart){
            return (isRepeating)? Constants.SMART_REPEATING_ALARM : Constants.SMART_SINGLE_ALARM;
        }else{
            return (isRepeating)? Constants.REPEATING_ALARM : Constants.SINGLE_ALARM;
        }

    }

    // Non-Repeating Alarm
    private void singleAlarm(Alarm a, int type){

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Constants.EXTRA_ALARM_TYPE, type);
        intent.putExtra(Constants.EXTRA_ALARM, a);
        int alarmId = a.getId();
        Calendar c = help.getTimeFromString(a.getTime());
        if (type == Constants.SINGLE_NO_DISABLE){
            Log.d(Constants.TAG_ALARM_CONTROLLER, "SINGLE NO DISABLE ");
            alarmId += 99999;
        }else if(type == Constants.STATS_ALARM){
            Log.d(Constants.TAG_ALARM_CONTROLLER, "STATISTICS ALARM ");
            alarmId += (99999*2);
            c = help.getTimeFromString(a.getDueTime());
        }
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (type == Constants.SMART_SINGLE_ALARM){
            Log.d(Constants.TAG_ALARM_CONTROLLER, "SMART ALARM " );
            c.add(Calendar.MINUTE, Constants.INTERVAL);
        }

        if(c.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
            c.add(c.DATE, 1);
        }

        PendingIntent mainIntent = PendingIntent.getActivity(context,0,new Intent(context, MainActivity.class),0);
        AlarmManager.AlarmClockInfo aInfo= new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), mainIntent);

        if (type != Constants.STATS_ALARM){
            aManager.setAlarmClock(aInfo, alarmIntent );
            Log.d(Constants.TAG_ALARM_CONTROLLER, "Single Alarm "+help.timePrintOut(c) );
        }else{
            aManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),alarmIntent);
            Log.d(Constants.TAG_ALARM_CONTROLLER, "Stats Alarm "+help.timePrintOut(c) );

        }

    }

    // Repeating Alarm
    private void repeatingAlarm(Alarm a, int type ){

        // Pending Intent with extras
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Constants.EXTRA_ALARM_TYPE, type);
        intent.putExtra(Constants.EXTRA_ALARM, a);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, a.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Activation Time
        Calendar c = help.getTimeFromString(a.getTime());

        if (type == Constants.SMART_REPEATING_ALARM){
            Log.d(Constants.TAG_ALARM_CONTROLLER, "SMART ALARM " );
            c.add(Calendar.MINUTE, Constants.INTERVAL);
        }

        // Updating Calendar with day to Activate
        c = getDayToActivate(c, a);

        // Set Alarm
        PendingIntent mainIntent = PendingIntent.getActivity(context,0,new Intent(context, MainActivity.class),0);
        AlarmManager.AlarmClockInfo aInfo= new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), mainIntent);

        aManager.setAlarmClock(aInfo, alarmIntent);

        Log.d(Constants.TAG_ALARM_CONTROLLER, "Repeating Alarm "+help.timePrintOut(c) + " For Day(s): "+ help.intArrayListToString(help.getActiveDays(a.getDays())));
    }

    // Stop Alarm
    private void stopAlarm(int id){
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (aManager != null) {
            aManager.cancel(alarmIntent);
            Log.d(Constants.TAG_ALARM_CONTROLLER, "Stop Alarm");
        }
    }

    // Get next day to activate for the repeating alarm
    private Calendar getDayToActivate(Calendar c, Alarm a){
        // Now Calendar
        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_WEEK);

        // Getting Day to Activate
        ArrayList<Integer> activeDays = help.getActiveDays(a.getDays());
        int dayToActivate = -1;
        for (int i: activeDays ) {
            if(now.getTimeInMillis() >= c.getTimeInMillis()){
                if(i > today){
                    dayToActivate = i;
                    break;
                }
            }else{
                if(i >= today){
                    dayToActivate = today;
                    break;
                }
            }
        }
        if(dayToActivate == -1){
            dayToActivate = activeDays.get(0);
        }

        if(dayToActivate == 1){
            c.set(Calendar.DAY_OF_WEEK, 7);
            c.add(c.DATE, 1);
        }
        else{
            c.set(Calendar.DAY_OF_WEEK, dayToActivate);
            if(c.getTimeInMillis() < now.getTimeInMillis()){
                c.add(c.DATE, 7);
            }
        }

        return c;
    }

}
