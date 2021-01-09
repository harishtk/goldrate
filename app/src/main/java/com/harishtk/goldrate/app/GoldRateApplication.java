package com.harishtk.goldrate.app;

import android.app.Application;

import timber.log.Timber;

public class GoldRateApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}