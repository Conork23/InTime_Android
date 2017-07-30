package com.intimealarm.conor.intime_app.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.intimealarm.conor.intime_app.database.DbHelper;
import com.intimealarm.conor.intime_app.models.Alarm;
import com.intimealarm.conor.intime_app.services.LocationService;
import com.intimealarm.conor.intime_app.services.NetworkService;
import com.intimealarm.conor.intime_app.utilities.Constants;
import com.intimealarm.conor.intime_app.utilities.Helper;
import com.intimealarm.conor.intime_app.utilities.NotificationHelper;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 23/01/2017.
 */
public class AlarmReceiver extends BroadcastReceiver{

    //Variables
    Context context;
    AlarmController aController;
    Helper help;
    NotificationHelper notify;
    DbHelper db;
    int type;

    // On Broadcast received
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        aController = new AlarmController(context);
        help = new Helper();
        notify = new NotificationHelper(context);
        db = new DbHelper(context);

        // Check what type of Alarm has activated
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setAllActive();
        }else {
            type = intent.getIntExtra(Constants.EXTRA_ALARM_TYPE, 0);
            Log.d("CALANDAR", "type  = " + type);

            switch (type) {
                case Constants.SINGLE_ALARM:
                    singleAlarm(intent);
                    break;
                case Constants.REPEATING_ALARM:
                    repeatingAlarm(intent);
                    break;
                case Constants.SMART_SINGLE_ALARM:
                case Constants.SMART_REPEATING_ALARM:
                    SmartAlarm(intent);
                    break;
                case Constants.SINGLE_NO_DISABLE:
                    singleNoDisable(intent);
                    break;
                case Constants.STATS_ALARM:
                    StatsAlarm(intent);
            }
        }

    }

    // Stats Alarm has Activated
    private void StatsAlarm(Intent intent) {
        Log.d(Constants.TAG_ALARM_RECIEVER, "ACTIVATED Statistics Alarm: "+help.timePrintOut(Calendar.getInstance()));
        Alarm a = (Alarm) intent.getSerializableExtra(Constants.EXTRA_ALARM);

        Intent start =  new Intent(context, LocationService.class);
        Log.d(Constants.TAG_ALARM_RECIEVER, "putting alarm extra "+ a.getPublic());
        start.putExtra(Constants.EXTRA_ALARM, a);
        Log.d(Constants.TAG_ALARM_RECIEVER, "Starting Location Service ");
        context.startService(start);
    }

    // Reset all alarm when system turns on
    private void setAllActive() {
        ArrayList<Alarm> alarms = db.allAlarms();

        for (Alarm a: alarms) {
            if (a.getActive()){
                aController.setAlarm(a);
            }

        }
    }

    // Single No Diable Alarm has Activated
    private void singleNoDisable(Intent intent) {
        Log.d(Constants.TAG_ALARM_RECIEVER, "ACTIVATED Single for repeating smart: "+help.timePrintOut(Calendar.getInstance()));
        Alarm a = (Alarm) intent.getSerializableExtra(Constants.EXTRA_ALARM);
        notify.makeNotification(a);
        aController.setAlarm(a);
    }

    // Single Alarm has Activated
    private void singleAlarm(Intent intent) {
        Log.d(Constants.TAG_ALARM_RECIEVER, "ACTIVATED Single: "+help.timePrintOut(Calendar.getInstance()));
        Alarm a = (Alarm) intent.getSerializableExtra(Constants.EXTRA_ALARM);
        notify.makeNotification(a);
        db.disableAlarm(a.getId());

    }

    // Repeating Alarm has Activated
    private void repeatingAlarm(Intent intent) {
        Alarm a = (Alarm) intent.getSerializableExtra(Constants.EXTRA_ALARM);
        Log.d(Constants.TAG_ALARM_RECIEVER, "ACTIVATED Repeating: "+help.timePrintOut(Calendar.getInstance()));
        notify.makeNotification(a);

        // Set Next Repeating Alarm;
        aController.setAlarm(a);
    }

    // Smart Alarm has Activated
    private void SmartAlarm(Intent intent) {
        final Alarm a = (Alarm) intent.getSerializableExtra(Constants.EXTRA_ALARM);
        Log.d(Constants.TAG_ALARM_RECIEVER, "ACTIVATED Smart: "+help.timePrintOut(Calendar.getInstance()));

        // Check if user wishes to provide anonymous data
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);
                boolean provideStats = prefs.getBoolean(Constants.SHARED_STATS, false);

                if (provideStats){
                    aController.setStatsAlarm(a);
                }
            }
        }).start();

        Intent start = new Intent(context, NetworkService.class);
        start.putExtra(Constants.EXTRA_ALARM, a);
        start.putExtra(Constants.EXTRA_ALARM_TYPE, type);
        context.startService(start);

    }
}
