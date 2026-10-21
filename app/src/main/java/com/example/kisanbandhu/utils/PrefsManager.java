
package com.example.kisanbandhu.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kisanbandhu.models.Soil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    // Crop keys
    public static final String KEY_SELECTED_CROP = "selected_crop";
    public static final String KEY_SOWING_DATE = "sowing_date_millis";

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );
    }

    /*
     * Each Firebase user gets separate preference keys.
     * A signed-out session cannot read another user's values.
     */
    private static String userKey(Context context, String key) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String uid = user != null
                ? user.getUid()
                : "signed_out";

        return uid + "_" + key;
    }

    public static void saveSoilProfile(Context context, Soil soil) {
        if (soil == null) return;

        SharedPreferences.Editor editor = prefs(context).edit();

        editor.putString(userKey(context, KEY_SOIL_TYPE), soil.getSoilType());
        editor.putString(userKey(context, KEY_SOIL_PH), soil.getSoilPH());
        editor.putString(userKey(context, KEY_FERTILITY), soil.getFertility());
        editor.putString(userKey(context, KEY_STATE), soil.getState());
        editor.putString(userKey(context, KEY_DISTRICT), soil.getDistrict());
        editor.putBoolean(userKey(context, KEY_IRRIGATED), soil.isIrrigated());
        editor.putString(userKey(context, KEY_SEASON), soil.getSeason());
        editor.putBoolean(userKey(context, KEY_PROFILE_SAVED), true);

        editor.apply();
    }

    public static boolean hasSoilProfile(Context context) {
        return prefs(context).getBoolean(
                userKey(context, KEY_PROFILE_SAVED),
                false
        );
    }

    public static Soil getSoilProfile(Context context) {
        if (!hasSoilProfile(context)) {
            return null;
        }

        SharedPreferences p = prefs(context);

        return new Soil(
                p.getString(userKey(context, KEY_SOIL_TYPE), ""),
                p.getString(userKey(context, KEY_SOIL_PH), ""),
                p.getString(userKey(context, KEY_FERTILITY), ""),
                p.getString(userKey(context, KEY_STATE), ""),
                p.getString(userKey(context, KEY_DISTRICT), ""),
                p.getBoolean(userKey(context, KEY_IRRIGATED), false),
                p.getString(userKey(context, KEY_SEASON), "")
        );
    }

    public static void saveString(
            Context context,
            String key,
            String value
    ) {
        SharedPreferences.Editor editor = prefs(context).edit();
        String scopedKey = userKey(context, key);

        if (value == null) {
            editor.remove(scopedKey);
        } else {
            editor.putString(scopedKey, value);
        }

        editor.apply();
    }

    public static String getString(
            Context context,
            String key,
            String defaultValue
    ) {
        return prefs(context).getString(
                userKey(context, key),
                defaultValue
        );
    }

    public static void saveLong(
            Context context,
            String key,
            long value
    ) {
        prefs(context).edit()
                .putLong(userKey(context, key), value)
                .apply();
    }

    public static long getLong(
            Context context,
            String key,
            long defaultValue
    ) {
        return prefs(context).getLong(
                userKey(context, key),
                defaultValue
        );
    }
}
