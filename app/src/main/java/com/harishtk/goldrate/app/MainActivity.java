package com.harishtk.goldrate.app;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;

import com.harishtk.goldrate.app.databinding.ActivityMainBinding;
import com.harishtk.goldrate.app.util.SharedPreferencesManager;
import com.harishtk.goldrate.app.work.GoldRateWorker;

public class MainActivity extends AppCompatActivity {

    public static final String CRAWL_URL = "https://thangamayil.com";

    private ActivityMainBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        viewBinding.textView.setText("Please wait..");

        checkSmsPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(this, getString(R.string.notification_channel), getString(R.string.notification_title));
        }
        checkScheduledWorker();

        final String val = SharedPreferencesManager.getPrefGoldRate22k(this);
        final long timestamp = SharedPreferencesManager.getPrefLastFetchedTimestamp(this);
        viewBinding.time.setText(new SimpleDateFormat("HH:mm:ss aa dd MMM yyyy").format(timestamp));
        viewBinding.goldRate.setText("22k: " + val);
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
        viewBinding.textView.setText("You will receive a notification as soon as on any update.");
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