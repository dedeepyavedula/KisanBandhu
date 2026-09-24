package com.example.kisanbandhu.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.kisanbandhu.activities.RemindersActivity;

/** Fires when a scheduled crop reminder is due and shows a notification. */
public class ReminderReceiver extends BroadcastReceiver {

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_ID = "id";
    private static final String CHANNEL_ID = "kisanbandhu_reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID, "Crop reminders", NotificationManager.IMPORTANCE_DEFAULT));
        }

        PendingIntent open = PendingIntent.getActivity(context, 0,
                new Intent(context, RemindersActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String message = intent.getStringExtra(EXTRA_MESSAGE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(intent.getStringExtra(EXTRA_TITLE))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(open)
                .setAutoCancel(true);
        try {
            nm.notify(intent.getIntExtra(EXTRA_ID, 0), builder.build());
        } catch (SecurityException ignored) {
            // notification permission denied - nothing to show
        }
    }
}
