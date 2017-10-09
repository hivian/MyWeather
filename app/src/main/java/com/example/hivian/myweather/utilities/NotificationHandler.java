package com.example.hivian.myweather.utilities;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by hivian on 10/7/17.
 */

public class NotificationHandler {
    private static int NOTIFICATION_ID = 1;
    private static Object service;

    public static void notify(Context context, int icon, String title, String message) {

        service = context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder n  = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(false);

        NotificationManager notificationManager = (NotificationManager) service;
        notificationManager.notify(NOTIFICATION_ID, n.build());

    }

    public static void cancelNotification(Context context) {
        service = context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationManager nm = (NotificationManager) service;
        nm.cancel(NOTIFICATION_ID);
    }

}
