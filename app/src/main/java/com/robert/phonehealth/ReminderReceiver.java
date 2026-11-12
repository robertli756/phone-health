package com.robert.phonehealth;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL = "cleanup_reminder";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        NotificationManager nm =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL, Strings.channelName(),
                    NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(ch);
        }
        Intent i = new Intent(ctx, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        android.app.Notification n;
        if (Build.VERSION.SDK_INT >= 26) {
            n = new android.app.Notification.Builder(ctx, CHANNEL)
                    .setSmallIcon(android.R.drawable.ic_menu_manage)
                    .setContentTitle(Strings.notifTitle())
                    .setContentText(Strings.notifText())
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
        } else {
            n = new android.app.Notification.Builder(ctx)
                    .setSmallIcon(android.R.drawable.ic_menu_manage)
                    .setContentTitle(Strings.notifTitle())
                    .setContentText(Strings.notifText())
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
        }
        nm.notify(1001, n);
    }
}
