package com.harishtk.goldrate.app.thread;

import android.os.Process;
import android.util.Log;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;

public class BackgroundThreadFactory implements ThreadFactory {
    public static final String TAG = BackgroundThreadFactory.class.getSimpleName();
    public static final int sTag = 1;

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("GoldSpider" + sTag);
        thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

        thread.setUncaughtExceptionHandler((t, e) ->
                Log.e(TAG, String.format(Locale.ENGLISH, "%s encountered an error: %s", thread.getName(), e.getMessage())));

        return thread;
    }
}