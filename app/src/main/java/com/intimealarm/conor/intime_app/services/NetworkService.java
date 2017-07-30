package com.intimealarm.conor.intime_app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intimealarm.conor.intime_app.network.Requests;
import com.intimealarm.conor.intime_app.database.DbHelper;
import com.intimealarm.conor.intime_app.logic.AlarmController;
import com.intimealarm.conor.intime_app.models.Alarm;
import com.intimealarm.conor.intime_app.utilities.Constants;
import com.intimealarm.conor.intime_app.utilities.Helper;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 23/02/2017.
 */

public class NetworkService extends Service {

    // Variables
    private Helper help;
    private Requests request;
    DbHelper db;
    AlarmController aController;
    int type;

    // On Service Create
    @Override
    public void onCreate() {
        super.onCreate();
        help = new Helper();
        request = new Requests();
        db = new DbHelper(getApplicationContext());
        aController = new AlarmController(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // On Service Start
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constants.TAG_ALARM_NETWORK_SERVICE, "onStartCommand: ");
        Alarm a = (Alarm) intent.getSerializableExtra(Constants.EXTRA_ALARM);
        type = intent.getIntExtra(Constants.EXTRA_ALARM_TYPE, 2);

        NetworkThread net = new NetworkThread(a);
        new Thread(net).start();

        return START_NOT_STICKY;
    }

    // Make Request to Web App on A seperate thread an parse result
    class NetworkThread implements Runnable {

        // vars
        Alarm a;
        NetworkThread(Alarm a) {
            this.a = a;
        }

        // Make Request
        public void run() {
            String to = db.getLoc(a.getTo()).getAddress();
            String from = db.getLoc(a.getFrom()).getAddress();
            String departureTime = help.departureTimePrintOut(a);
            String response = request.getInTimeResponse(a, departureTime, to, from);

            parseResponse(response);

        }

        // Parse Response
        private void parseResponse(String jsonString){
            JsonParser parser = new JsonParser();
            JsonObject jObj = parser.parse(jsonString).getAsJsonObject();
            String status = jObj.get("status").getAsString();
            long arrival_time = -1;
            if (status.equals("OK")){
                arrival_time = jObj.get("newTimeStamp").getAsLong();
            }

            aController.setAlarm(a, arrival_time, type);

        }
    }
}
