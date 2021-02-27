package com.harishtk.goldrate.app.util

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private var sInstance: SharedPreferences? = null
    private const val APP_SETTINGS = "APP_SETTINGS"

    // properties
    private const val PREF_GOLD_RATE_22K = "pref-gold-rate-22k"
    private const val PREF_LAST_FETCHED_TIMESTAMP = "pref-last-fetched-timestamp"
    fun reset(context: Context) {
        getSharedPreferences(context)!!.edit().clear().apply()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences? {
        if (sInstance == null) {
            sInstance = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
        }
        return sInstance
    }

    @JvmStatic
    fun getPrefGoldRate22k(context: Context): String? {
        return getSharedPreferences(context)!!.getString(PREF_GOLD_RATE_22K, "N/A")
    }

    @JvmStatic
    fun setPrefGoldRate22k(context: Context, newValue: String?) {
        getSharedPreferences(context)!!.edit()
                .putString(PREF_GOLD_RATE_22K, newValue).apply()
    }

    @JvmStatic
    fun getPrefLastFetchedTimestamp(context: Context): Long {
        return getSharedPreferences(context)!!.getLong(PREF_LAST_FETCHED_TIMESTAMP, 0L)
    }

    @JvmStatic
    fun setPrefLastFetchedTimestamp(context: Context, timestamp: Long) {
        getSharedPreferences(context)!!.edit()
                .putLong(PREF_LAST_FETCHED_TIMESTAMP, timestamp).apply()
    }
}