package com.harishtk.goldrate.app.work;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ForegroundInfo;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.harishtk.goldrate.app.R;
import com.harishtk.goldrate.app.thread.GoldSpiderCallable;
import com.harishtk.goldrate.app.util.Constants;
import com.harishtk.goldrate.app.util.guava.Optional;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GoldRateWorker extends Worker {
    private static final String TAG = GoldRateWorker.class.getSimpleName();

    public static final String WORKER_TAG = GoldRateWorker.class.getCanonicalName() + ".gold-rate-worker";
    private static final int WORKER_NOTIFICATION_ID = 1;

    public static final String KEY_GOLD_18K = "Gold 18k";
    public static final String KEY_GOLD_22K = "Gold 22k";
    public static final String KEY_GOLD_24K = "Gold 24k";
    public static final String KEY_SILVER = "Silver";

    public static final String CRAWL_URL = "https://thangamayil.com";

    private Context mContext;

    private GoldRateWorker(@NonNull Context context, @NonNull WorkerParameters parameters) {
        super(context, parameters);

        mContext = context;
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        setForegroundAsync(createForegroundInfo());

        final Bundle b = new Bundle();
        b.putString("url", CRAWL_URL);
        GoldSpiderCallable<?> callable = new GoldSpiderCallable<>(b);
        Optional<Map<String, String>> optional = callable.call();
        if (optional.isPresent()) {
            final String val = optional.get().get(KEY_GOLD_22K);
            // show notification
            Notification notification = new NotificationCompat.Builder(mContext,
                    mContext.getString(R.string.notification_channel))
                    .setContentTitle("Gold Rate Updates")
                    .setContentText("Today gold rate for " + KEY_GOLD_22K + ": " + val)
                    .setSmallIcon(R.drawable.ic_gold_coin)
                    .build();
            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(Constants.NOTIFICATION_ID, notification);

            sendSms(KEY_GOLD_22K, val);
            return Result.success();
        } else {
            return Result.failure();
        }
    }

    private void sendSms(String keyGold22k, String val) {
        String phoneNo = "9944194330";
        String msg = "Gold Rate Updates\n" + "Today gold rate for " + KEY_GOLD_22K + ": " + val;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @NonNull
    private ForegroundInfo createForegroundInfo() {
        Context context = getApplicationContext();
        final String id = context.getString(R.string.worker_notification_channel);
        final String title = context.getString(R.string.worker_notification_title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context);
        }

        Notification notification = new NotificationCompat.Builder(context, id)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(context.getString(R.string.fetching_gold_rates))
                .setSmallIcon(R.drawable.ic_gold_coin)
                .setOngoing(true)
                .build();
        return new ForegroundInfo(WORKER_NOTIFICATION_ID, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(@NonNull Context context) {
        NotificationChannel notificationChannel = new NotificationChannel(context.getString(R.string.worker_notification_channel),
                context.getString(R.string.worker_notification_title), NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableVibration(false);
        notificationChannel.setSound(null, null);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }

    public static class GoldRateWorkerBuilder {
        PeriodicWorkRequest periodicWorkRequest;

        public GoldRateWorkerBuilder(@NonNull String url) {
            Data data = new Data.Builder()
                    .putString(Constants.EXTRA_URL, url)
                    .build();
            Constraints.Builder constraintsBuilder = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresCharging(false)
                    .setRequiresBatteryNotLow(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                constraintsBuilder.setTriggerContentMaxDelay(Duration.ZERO);
            }
            periodicWorkRequest = new PeriodicWorkRequest.Builder(GoldRateWorker.class, 15, TimeUnit.MINUTES)
                    .setInputData(data)
                    .setConstraints(constraintsBuilder.build())
                    .addTag(WORKER_TAG)
                    .build();
        }

        public PeriodicWorkRequest build() {
            return periodicWorkRequest;
        }
    }
}