package com.intimealarm.conor.intime_app.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.intimealarm.conor.intime_app.utilities.Constants;
import com.intimealarm.conor.intime_app.utilities.NotificationHelper;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 31/01/2017.
 */

public class RingtoneService extends Service{

   // Variables
    Uri ringtoneUri;
    Ringtone player;

    // Constructor
    public RingtoneService() { }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // On Create
    @Override
    public void onCreate() { }

    // On Service Start
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get Ringtone Manager
        ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (ringtoneUri == null){
            ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        // Add Attributes to Ringtone
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        // Stop Playing Rington
        if (intent.getBooleanExtra(Constants.EXTRA_STOP_RINGTONE, false)){
            Log.d(Constants.TAG_ALARM_RINGTONE, "onStartCommand: stop");
            if (player != null && player.isPlaying()){
                player.stop();
            }
            closeNotification(intent.getIntExtra(Constants.EXTRA_ALARM_ID,-1));
        }
        // Start Ringtone
        else{
            Log.d(Constants.TAG_ALARM_RINGTONE, "onStartCommand: start");
            if (player == null){
                player = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
                player.setAudioAttributes(attributes);
            }
            startRingtone();
        }

        return START_NOT_STICKY;
    }

    // Close Notification
    private void closeNotification(int id) {
        NotificationHelper nHelper = new NotificationHelper(getApplicationContext());
        nHelper.cancelNotification(id);
    }

    // Start Playing Ringtone
    private void startRingtone(){
        Log.d(Constants.TAG_ALARM_RINGTONE, "startRingtone: ringing...");
        player.play();

    }
}
