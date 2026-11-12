package com.robert.phonehealth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(ctx, ReminderReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, i,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 21);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            if (c.getTimeInMillis() <= System.currentTimeMillis()) {
                c.add(Calendar.DAY_OF_YEAR, 1);
            }
            am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pi);
        }
    }
}
