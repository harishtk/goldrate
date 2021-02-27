package com.harishtk.goldrate.app.thread

import android.os.Process
import android.util.Log
import java.util.*
import java.util.concurrent.ThreadFactory

class BackgroundThreadFactory: ThreadFactory {

    override fun newThread(r: Runnable?): Thread {
        return Thread(r).apply {
            name = "GoldSpider$sTag"
            priority = Process.THREAD_PRIORITY_BACKGROUND
            uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _: Thread?, e: Throwable -> Log.e(TAG, String.format(Locale.ENGLISH, "%s encountered an error: %s", name, e.message)) }
        }
    }

    companion object {
        val TAG: String = BackgroundThreadFactory::class.java.simpleName
        const val sTag: Int = 1
    }

}