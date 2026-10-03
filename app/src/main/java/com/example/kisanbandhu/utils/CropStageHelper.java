package com.example.kisanbandhu.utils;

import android.content.Context;

import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.GrowthStage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CropStageHelper {

    public static final long DAY_MS =
            24L * 60 * 60 * 1000;

    /*
     * Compatibility method used by ReminderManager.
     *
     * The actual detailed crop guide is now loaded from Firestore.
     * This method only provides basic reminder stages so the existing
     * ReminderManager continues to work.
     */
    public static List<GrowthStage> getStages(Crop crop) {

        int total = getTotalDays(crop);

        List<GrowthStage> stages =
                new ArrayList<>();

        stages.add(
                new GrowthStage(
                        "Germination",
                        0,
                        (int) (total * 0.10),
                        "Germination and early seedling development."
                )
        );

        stages.add(
                new GrowthStage(
                        "Vegetative Growth",
                        (int) (total * 0.10),
                        (int) (total * 0.40),
                        "Vegetative growth and plant development."
                )
        );

        stages.add(
                new GrowthStage(
                        "Flowering",
                        (int) (total * 0.40),
                        (int) (total * 0.65),
                        "Flowering and reproductive development."
                )
        );

        stages.add(
                new GrowthStage(
                        "Fruit / Grain Development",
                        (int) (total * 0.65),
                        (int) (total * 0.90),
                        "Fruit, grain or pod development."
                )
        );

        stages.add(
                new GrowthStage(
                        "Maturity & Harvest",
                        (int) (total * 0.90),
                        total,
                        "Crop reaches maturity and is ready for harvest."
                )
        );

        return stages;
    }

    public static int getTotalDays(Crop crop) {

        Matcher matcher =
                Pattern.compile("\\d+")
                        .matcher(
                                crop.getGrowthDuration()
                        );

        int total = 120;

        while (matcher.find()) {
            total =
                    Integer.parseInt(
                            matcher.group()
                    );
        }

        return total;
    }

    public static boolean hasSowingDate(
            Context context) {

        return PrefsManager.getLong(
                context,
                PrefsManager.KEY_SOWING_DATE,
                0
        ) > 0;
    }

    public static long getSowingDate(
            Context context) {

        return PrefsManager.getLong(
                context,
                PrefsManager.KEY_SOWING_DATE,
                0
        );
    }

    public static int getDaysSinceSowing(
            Context context) {

        long diff =
                System.currentTimeMillis()
                        - getSowingDate(context);

        return (int) Math.floorDiv(
                diff,
                DAY_MS
        );
    }

    public static int getCurrentStageIndex(
            Context context,
            List<FirestoreCropGuideManager.StageData> stages) {

        if (!hasSowingDate(context)
                || stages == null
                || stages.isEmpty()) {

            return -1;
        }

        int days =
                getDaysSinceSowing(context);

        if (days < 0) {
            return -1;
        }

        for (int i = 0; i < stages.size(); i++) {

            if (days < stages.get(i).getEndDay()) {
                return i;
            }
        }

        return stages.size() - 1;
    }

    public static void selectCrop(
            Context context,
            String cropName) {

        String previous =
                PrefsManager.getString(
                        context,
                        PrefsManager.KEY_SELECTED_CROP,
                        null
                );

        if (!cropName.equals(previous)) {

            PrefsManager.saveLong(
                    context,
                    PrefsManager.KEY_SOWING_DATE,
                    0
            );

            ReminderManager.cancelAll(context);
        }

        PrefsManager.saveString(
                context,
                PrefsManager.KEY_SELECTED_CROP,
                cropName
        );
    }
    public static void selectCrop(
            Context context,
            Crop crop) {

        if (crop == null) {
            return;
        }

        selectCrop(context, crop.getName());
    }
}