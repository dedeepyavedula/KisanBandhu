// FILE: app/src/main/java/com/example/kisanbandhu/utils/PrefsManager.java
package com.example.kisanbandhu.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kisanbandhu.models.Soil;

/**
 * Central place for reading/writing simple app data using
 * SharedPreferences (as required by the project spec instead of a
 * database for this college version).
 *
 * Every other Activity should go through this class rather than
 * calling getSharedPreferences() directly - keeps all the key names
 * in one place and avoids typos.
 */
public class PrefsManager {

    private static final String PREFS_NAME = "KisanBandhuPrefs";

    // Soil / farmer profile keys
    private static final String KEY_SOIL_TYPE = "soil_type";
    private static final String KEY_SOIL_PH = "soil_ph";
    private static final String KEY_FERTILITY = "fertility";
    private static final String KEY_STATE = "state";
    private static final String KEY_DISTRICT = "district";
    private static final String KEY_IRRIGATED = "irrigated";
    private static final String KEY_SEASON = "season";
    private static final String KEY_PROFILE_SAVED = "profile_saved";

    // Keys used in later phases (declared now so all Activities can rely on them)
    public static final String KEY_SELECTED_CROP = "selected_crop";
    public static final String KEY_SOWING_DATE = "sowing_date_millis";

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Saves the farmer's soil profile. Called from SoilInputActivity. */
    public static void saveSoilProfile(Context context, Soil soil) {
        SharedPreferences.Editor editor = prefs(context).edit();
        editor.putString(KEY_SOIL_TYPE, soil.getSoilType());
        editor.putString(KEY_SOIL_PH, soil.getSoilPH());
        editor.putString(KEY_FERTILITY, soil.getFertility());
        editor.putString(KEY_STATE, soil.getState());
        editor.putString(KEY_DISTRICT, soil.getDistrict());
        editor.putBoolean(KEY_IRRIGATED, soil.isIrrigated());
        editor.putString(KEY_SEASON, soil.getSeason());
        editor.putBoolean(KEY_PROFILE_SAVED, true);
        editor.apply();
    }

    /** Returns true if the farmer has filled the soil form at least once. */
    public static boolean hasSoilProfile(Context context) {
        return prefs(context).getBoolean(KEY_PROFILE_SAVED, false);
    }

    /**
     * Rebuilds a Soil object from what was saved earlier.
     * Returns null if nothing has been saved yet - callers must check
     * hasSoilProfile() first (or handle null).
     */
    public static Soil getSoilProfile(Context context) {
        if (!hasSoilProfile(context)) {
            return null;
        }
        SharedPreferences p = prefs(context);
        return new Soil(
                p.getString(KEY_SOIL_TYPE, ""),
                p.getString(KEY_SOIL_PH, ""),
                p.getString(KEY_FERTILITY, ""),
                p.getString(KEY_STATE, ""),
                p.getString(KEY_DISTRICT, ""),
                p.getBoolean(KEY_IRRIGATED, false),
                p.getString(KEY_SEASON, "")
        );
    }

    // ---- Generic helpers reused by later phases (crop selection, sowing date) ----

    public static void saveString(Context context, String key, String value) {
        prefs(context).edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        return prefs(context).getString(key, defaultValue);
    }

    public static void saveLong(Context context, String key, long value) {
        prefs(context).edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, String key, long defaultValue) {
        return prefs(context).getLong(key, defaultValue);
    }
}
