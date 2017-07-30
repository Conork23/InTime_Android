package com.intimealarm.conor.intime_app.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intimealarm.conor.intime_app.database.DbHelper;
import com.intimealarm.conor.intime_app.models.Alarm;
import com.intimealarm.conor.intime_app.models.Evaluation;
import com.intimealarm.conor.intime_app.network.Requests;
import com.intimealarm.conor.intime_app.utilities.Constants;
import com.intimealarm.conor.intime_app.utilities.Helper;


import java.util.Calendar;

import static android.R.attr.logo;
import static android.R.attr.type;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 06/03/2017.
 */

public class EvaluationService extends Service {

    // Variables
    private Requests request;
    private DbHelper db;
    private Helper help;
    private SharedPreferences sharedPref;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // On Service Create Method
    @Override
    public void onCreate() {
        super.onCreate();
        request = new Requests();
        db = new DbHelper(getApplicationContext());
        help = new Helper();
        sharedPref = getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);
    }

    // On Service Start
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Boolean isPost = intent.getBooleanExtra(Constants.EXTRA_EVAL_POST, false);
        // Creating Evaluation object ot post to Web App
        if (isPost){
            Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();
            Gson gson = new Gson();
            String json = sharedPref.getString(Constants.SHARED_EVAL, "");
            Evaluation eval = gson.fromJson(json, Evaluation.class);

            //Getting Actual time
            Calendar original = help.timePrintOutToCal(eval.getTime());
            Calendar now = Calendar.getInstance();
            Log.d("Alarm", "onStartCommand: diff orig " + original.getTimeInMillis() +"  "+ help.timePrintOut(original) );
            Log.d("Alarm", "onStartCommand: diff now " + now.getTimeInMillis()+  "  "+help.timePrintOut(now));


            long diffMili = now.getTimeInMillis() - original.getTimeInMillis();
            Log.d("Alarm", "onStartCommand: diff mills" + diffMili);
            int diffMinutes = (int) (diffMili / (1000*60));
            Log.d("Alarm", "onStartCommand: diff mins " + diffMinutes);

            eval.setActual(diffMinutes+" mins");

            PostStats post = new PostStats(eval);
            new Thread(post).start();

        }

        // Making request to get estimation on travel time
        else{
            Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
            Alarm a = (Alarm) intent.getSerializableExtra(Constants.EXTRA_ALARM);
            boolean isPublic = intent.getBooleanExtra(Constants.EXTRA_ISPUBLIC, false);
            GetEstimate get = new GetEstimate(a,isPublic);
            new Thread(get).start();

        }

        return START_NOT_STICKY;
    }

    // Get Estimation from server
    class GetEstimate implements Runnable{
        Alarm a;
        String to, from, departureTime;
        boolean isPublic;

        public GetEstimate(Alarm a, boolean isPublic) {
            this.a = a;
            this.to = db.getLoc(a.getTo()).getAddress();
            this.from = db.getLoc(a.getFrom()).getAddress();
            this.departureTime = help.timePrintOut(Calendar.getInstance());
            this.isPublic = isPublic;
        }

        @Override
        public void run() {
            String response = request.getInTimeResponse(a, departureTime, to, from);
            parseResponse(response);

        }

        private void parseResponse(String response) {
            JsonParser parser = new JsonParser();
            JsonObject jObj = parser.parse(response).getAsJsonObject();
            String status = jObj.get("status").getAsString();
            String arrival_time = "";
            if (status.equals("OK")){
                arrival_time = jObj.get("calculated_duration").getAsString();
            }

            Evaluation eval = new Evaluation(from, departureTime, arrival_time, to,isPublic);
            SharedPreferences.Editor editor = sharedPref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(eval);
            editor.putString(Constants.SHARED_EVAL, json);
            editor.apply();
        }
    }

    // Post evaluation to server
    class PostStats implements Runnable{
        Evaluation eval;
        public PostStats(Evaluation eval) {
            this.eval = eval;
        }

        @Override
        public void run() {
            request.sendStats(eval);
        }
    }
}
