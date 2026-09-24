package com.example.kisanbandhu.utils;

import android.content.Context;

import com.example.kisanbandhu.data.CropGuideData;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.GrowthStage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns a crop + sowing date into growth stages and "what stage am I in today".
 * Stage lengths are fractions of the crop's total duration.
 */
public class CropStageHelper {

    public static final long DAY_MS = 24L * 60 * 60 * 1000;

    // Stage boundaries as a fraction of total duration (5 stages)
    private static final double[] BOUNDS = {0.0, 0.10, 0.40, 0.65, 0.90, 1.0};

    /** Uses the longest number in text like "120-150 days". */
    public static int getTotalDays(Crop crop) {
        Matcher m = Pattern.compile("\\d+").matcher(crop.getGrowthDuration());
        int total = 120;
        while (m.find()) total = Integer.parseInt(m.group());
        return total;
    }

    public static List<GrowthStage> getStages(Crop crop) {
        int total = getTotalDays(crop);
        String[][] text = CropGuideData.getStageText(crop.getName());
        List<GrowthStage> stages = new ArrayList<>();
        for (int i = 0; i < text.length && i < BOUNDS.length - 1; i++) {
            int start = (int) Math.round(total * BOUNDS[i]);
            int end = (int) Math.round(total * BOUNDS[i + 1]);
            stages.add(new GrowthStage(text[i][0], start, end, text[i][1]));
        }
        return stages;
    }

    public static boolean hasSowingDate(Context context) {
        return PrefsManager.getLong(context, PrefsManager.KEY_SOWING_DATE, 0) > 0;
    }

    public static long getSowingDate(Context context) {
        return PrefsManager.getLong(context, PrefsManager.KEY_SOWING_DATE, 0);
    }

    /** Days since sowing (negative = sowing is in the future). Only valid if hasSowingDate. */
    public static int getDaysSinceSowing(Context context) {
        long diff = System.currentTimeMillis() - getSowingDate(context);
        return (int) Math.floorDiv(diff, DAY_MS);
    }

    /** Index of the current stage, -1 if not sown yet / no date. Past the end -> last stage. */
    public static int getCurrentStageIndex(Context context, List<GrowthStage> stages) {
        if (!hasSowingDate(context)) return -1;
        int days = getDaysSinceSowing(context);
        if (days < 0) return -1;
        for (int i = 0; i < stages.size(); i++) {
            if (days < stages.get(i).getEndDay()) return i;
        }
        return stages.size() - 1;
    }

    /**
     * Saves the chosen crop. If it is a different crop from before, the old
     * sowing date and reminders no longer apply, so they are cleared.
     */
    public static void selectCrop(Context context, Crop crop) {
        String previous = PrefsManager.getString(context, PrefsManager.KEY_SELECTED_CROP, null);
        if (!crop.getName().equals(previous)) {
            PrefsManager.saveLong(context, PrefsManager.KEY_SOWING_DATE, 0);
            ReminderManager.cancelAll(context);
        }
        PrefsManager.saveString(context, PrefsManager.KEY_SELECTED_CROP, crop.getName());
    }
}
