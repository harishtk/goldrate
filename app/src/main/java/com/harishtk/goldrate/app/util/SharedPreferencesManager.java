package com.harishtk.goldrate.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static SharedPreferences sInstance;

    private static final String APP_SETTINGS = "APP_SETTINGS";

    // properties
    private static final String PREF_GOLD_RATE_22K = "pref-gold-rate-22k";
    private static final String PREF_LAST_FETCHED_TIMESTAMP = "pref-last-fetched-timestamp";
    // other properties...

    private SharedPreferencesManager() {

    }

    public static void reset(Context context) {
        getSharedPreferences(context).edit().clear().apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        if (sInstance == null) {
            sInstance = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        }
        return sInstance;
    }

    public static String getPrefGoldRate22k(Context context) {
        return getSharedPreferences(context).getString(PREF_GOLD_RATE_22K, "N/A");
    }

    public static void setPrefGoldRate22k(Context context, String newValue) {
        getSharedPreferences(context).edit()
                .putString(PREF_GOLD_RATE_22K, newValue).apply();
    }

    public static long getPrefLastFetchedTimestamp(Context context) {
        return getSharedPreferences(context).getLong(PREF_LAST_FETCHED_TIMESTAMP, 0L);
    }

    public static void setPrefLastFetchedTimestamp(Context context, long timestamp) {
        getSharedPreferences(context).edit()
                .putLong(PREF_GOLD_RATE_22K, timestamp).apply();
    }
}