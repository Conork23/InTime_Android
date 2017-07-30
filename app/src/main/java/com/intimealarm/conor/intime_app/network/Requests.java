package com.intimealarm.conor.intime_app.network;

import android.util.Log;

import com.google.gson.Gson;
import com.intimealarm.conor.intime_app.models.Alarm;
import com.intimealarm.conor.intime_app.models.Evaluation;
import com.intimealarm.conor.intime_app.models.Statistic;
import com.intimealarm.conor.intime_app.utilities.Constants;
import com.intimealarm.conor.intime_app.utilities.Helper;


import java.io.IOException;
import java.util.Calendar;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 23/02/2017.
 */

public class Requests {

    // Variables
    OkHttpClient client;
    private Helper help;

    // Constructor
    public Requests(){
        client = new OkHttpClient();
        help = new Helper();
    }

    // Get Travel Time from web app
    public String getInTimeResponse(Alarm a, String time, String to, String from){
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme("https")
                .host("intimealarm.com")
                .addPathSegment("api")
                .addPathSegment("updatetime")
                .addPathSegment((a.getPublic())? "public" : "private")
                .addQueryParameter("time", time)
                .addQueryParameter("to", to)
                .addQueryParameter("from", from);

        if (a.getPublic()){
            urlBuilder.addQueryParameter("tmodes", help.arrToString(a.getTmodes()));
        }

        HttpUrl url = urlBuilder.build();

        Log.d(Constants.TAG_ALARM_REQUESTS, "getInTimeResponse: "+url.toString());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return  response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    // Send Evaluation object to web app
    public void sendStats(Evaluation eval){
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new Gson().toJson(eval));

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("intimealarm.com")
                .addPathSegment("stats")
                .addPathSegment("eval")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Send Statistics object to web app
    public void sendAutomatedStatistic(final Statistic s){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, new Gson().toJson(s));

                HttpUrl url = new HttpUrl.Builder()
                        .scheme("https")
                        .host("intimealarm.com")
                        .addPathSegment("stats")
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .build();

                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
