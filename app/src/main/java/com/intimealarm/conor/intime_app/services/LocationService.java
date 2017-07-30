package com.intimealarm.conor.intime_app.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import com.intimealarm.conor.intime_app.models.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.intimealarm.conor.intime_app.database.DbHelper;
import com.intimealarm.conor.intime_app.models.Alarm;
import com.intimealarm.conor.intime_app.models.Statistic;
import com.intimealarm.conor.intime_app.network.Requests;
import com.intimealarm.conor.intime_app.utilities.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 26/04/2017.
 */

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Variables
    GoogleApiClient googleApiClient;
    android.location.Location loc;
    Alarm alarm;
    DbHelper db;
    Location destination, origin;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // On Service Start - creating and connecting to Google Api Client
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constants.TAG_L_SERVICE, "Location Service Connected");
        db = new DbHelper(getApplicationContext());
        alarm = (Alarm) intent.getSerializableExtra(Constants.EXTRA_ALARM);
        destination = db.getLoc(alarm.getTo());
        origin = db.getLoc(alarm.getFrom());
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();
        Log.d(Constants.TAG_L_SERVICE, "Waiting for Connection");

        return START_REDELIVER_INTENT;
    }

    // On Service Destroy
    @Override
    public void onDestroy() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onDestroy();
    }

    // On Connection to Google Api Client
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d(Constants.TAG_L_SERVICE, "Google Client Connected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Getting Location
            loc = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (loc != null) {
                checkLocation(loc);
            }else{
                Log.d(Constants.TAG_L_SERVICE, "Location: null ");
            }
        }
    }

    // Checking if devices location is with in a radius of the target target location
    private void checkLocation(android.location.Location loc) {
        double deviceLat = loc.getLatitude();
        double deviceLng = loc.getLongitude();

        double latLower = deviceLat - Constants.RADIUS;
        double latUpper = deviceLat + Constants.RADIUS;
        double lngLower = deviceLng - Constants.RADIUS;
        double lngUpper = deviceLng + Constants.RADIUS;

        boolean hasArrived = false;

        if ((deviceLat >= latLower && deviceLat <= latUpper) && (deviceLng >= lngLower && deviceLng <= lngUpper)){
            hasArrived = true;
        }

        Statistic stat = new Statistic(origin.getAddress(), destination.getAddress(), alarm.getPublic(), hasArrived);

        Log.d(Constants.TAG_L_SERVICE, "Sending Request");
        new Requests().sendAutomatedStatistic(stat);

    }

    // On Connection to Google Api Client Suspended
    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    // On Connection to Google Api Client failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(Constants.TAG_L_SERVICE, "Google Client Connection Failed: " + connectionResult.getErrorMessage());


    }
}
