package com.intimealarm.conor.intime_app.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.widget.Button;
import android.widget.RemoteViews;

import com.intimealarm.conor.intime_app.R;
import com.intimealarm.conor.intime_app.services.RingtoneService;
import com.intimealarm.conor.intime_app.models.Alarm;

import static android.support.v7.app.NotificationCompat.CATEGORY_ALARM;
import static android.support.v7.app.NotificationCompat.PRIORITY_MAX;
import static android.support.v7.app.NotificationCompat.VISIBILITY_PUBLIC;


/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 30/01/2017.
 */

public class NotificationHelper {

    // Variables
    Context context;
    NotificationManager nManager;

    // Constructor
    public NotificationHelper(Context c) {
        this.context = c;
        nManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    // Method to make a notification
    public void makeNotification(Alarm a) {

        Intent i = new Intent(context, RingtoneService.class);
        i.putExtra(Constants.EXTRA_ALARM_ID, a.getId());
        i.putExtra(Constants.EXTRA_STOP_RINGTONE, true);
        PendingIntent mainIntent = PendingIntent.getService(context,a.getId(),i,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_action_name)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(context.getString(R.string.notification_title))
                        .addAction(android.R.drawable.ic_delete, "Dismiss", mainIntent)
                        .setCategory(CATEGORY_ALARM)
                        .setPriority(PRIORITY_MAX)
                        .setVisibility(VISIBILITY_PUBLIC)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setOngoing(true);

        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.notification);
        view.setOnClickPendingIntent(R.id.notificationBtn, mainIntent);

        mBuilder.setContent(view);

        Intent start = new Intent(context, RingtoneService.class);
        start.putExtra(Constants.EXTRA_STOP_RINGTONE, false);
        context.startService(start);
        nManager.notify(a.getId(), mBuilder.build());
    }

    // Cancel Notification
    public void cancelNotification(int id){
       if (id == -1){
           nManager.cancelAll();
       }
        nManager.cancel(id);
    }
}
