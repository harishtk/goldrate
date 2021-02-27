package com.harishtk.goldrate.app

import android.app.Application
import timber.log.Timber

class GoldRateApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}