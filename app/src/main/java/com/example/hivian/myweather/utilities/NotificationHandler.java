package com.example.hivian.myweather.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.example.hivian.myweather.R;

/**
 * Created by hivian on 10/7/17.
 */

public class NotificationHandler {
    private NotificationManager nm;

    public NotificationManager notify(Context context, int icon,
                                                 String title, String message) {
        NotificationCompat.Builder n  = new NotificationCompat.Builder(context)
                .setContentTitle("PDownload")
                .setContentText("Download in progress")
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, n.build());

        return notificationManager;
    }


}
