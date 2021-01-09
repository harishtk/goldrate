package com.harishtk.goldrate.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.harishtk.goldrate.app.thread.BackgroundThreadFactory;
import com.harishtk.goldrate.app.thread.GoldSpiderCallable;
import com.harishtk.goldrate.app.util.guava.Optional;
import com.harishtk.goldrate.app.work.GoldRateWorker;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static final String CRAWL_URL = "https://thangamayil.com";

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        textView.setText("Please wait..");

        checkSmsPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(this, getString(R.string.notification_channel), getString(R.string.notification_title));
        }
        checkScheduledWorker();
    }

    private void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 200);
        }
    }

    private void checkScheduledWorker() {
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(GoldRateWorker.WORKER_TAG,
                        ExistingPeriodicWorkPolicy.KEEP,
                        new GoldRateWorker.GoldRateWorkerBuilder(CRAWL_URL)
                                .build());
        textView.setText("You will receive a notification as soon as on any update.");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(@NonNull Context context, @NonNull String channelId, @NonNull String title) {
        NotificationChannel notificationChannel = new NotificationChannel(channelId,
                title, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableVibration(false);
        notificationChannel.setSound(null, null);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }
}