package com.example.kisanbandhu.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.GrowthStage;
import com.example.kisanbandhu.models.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds reminders from the sowing date (one per growth stage, at 8:00 AM on
 * the stage's first day) and schedules them with AlarmManager. Alarms are
 * inexact (no special permission needed) and are not restored after a phone
 * reboot - the farmer can simply tap "Schedule" again.
 */
public class ReminderManager {

    private static final String KEY_COUNT = "reminder_alarm_count";
    private static final long EIGHT_AM_MS = 8L * 60 * 60 * 1000;

    public static List<Reminder> buildReminders(Crop crop, long sowingMillis) {
        List<Reminder> list = new ArrayList<>();
        for (GrowthStage s : CropStageHelper.getStages(crop)) {
            long time = sowingMillis + s.getStartDay() * CropStageHelper.DAY_MS + EIGHT_AM_MS;
            list.add(new Reminder(crop.getEmoji() + " " + crop.getName() + ": " + s.getName(),
                    s.getActivity(), time));
        }
        return list;
    }

    /** Cancels old alarms and schedules all future reminders. Returns how many were scheduled. */
    public static int scheduleAll(Context context, Crop crop, long sowingMillis) {
        cancelAll(context);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return 0;

        List<Reminder> reminders = buildReminders(crop, sowingMillis);
        long now = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; i < reminders.size(); i++) {
            Reminder r = reminders.get(i);
            if (r.getTimeMillis() <= now) continue;
            Intent intent = new Intent(context, ReminderReceiver.class);
            intent.putExtra(ReminderReceiver.EXTRA_TITLE, r.getTitle());
            intent.putExtra(ReminderReceiver.EXTRA_MESSAGE, r.getMessage());
            intent.putExtra(ReminderReceiver.EXTRA_ID, i);
            am.set(AlarmManager.RTC_WAKEUP, r.getTimeMillis(), pending(context, i, intent));
            count++;
        }
        PrefsManager.saveLong(context, KEY_COUNT, reminders.size());
        return count;
    }

    public static void cancelAll(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        int saved = (int) PrefsManager.getLong(context, KEY_COUNT, 0);
        for (int i = 0; i < saved; i++) {
            am.cancel(pending(context, i, new Intent(context, ReminderReceiver.class)));
        }
        PrefsManager.saveLong(context, KEY_COUNT, 0);
    }

    private static PendingIntent pending(Context context, int requestCode, Intent intent) {
        return PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
