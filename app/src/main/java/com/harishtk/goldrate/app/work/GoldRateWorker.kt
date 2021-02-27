package com.harishtk.goldrate.app.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.harishtk.goldrate.app.R
import com.harishtk.goldrate.app.thread.GoldSpiderCallable
import com.harishtk.goldrate.app.util.Constants
import com.harishtk.goldrate.app.util.SharedPreferencesManager.setPrefGoldRate22k
import com.harishtk.goldrate.app.util.SharedPreferencesManager.setPrefLastFetchedTimestamp
import com.harishtk.goldrate.app.util.guava.Optional
import java.time.Duration
import java.util.concurrent.TimeUnit


class GoldRateWorker constructor(
        var context: Context,
        workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    @NonNull
    override fun doWork(): Result {
        val b = Bundle()
        b.putString("url", CRAWL_URL)
        val callable = GoldSpiderCallable<Optional<Map<String, String>>>(b)
        val optional: Optional<Map<String, String>> = callable.call()
        return if (optional.isPresent) {
            val `val`: String? = optional.get()[KEY_GOLD_22K]
            // show notification
            val notification = NotificationCompat.Builder(applicationContext,
                    context.getString(R.string.notification_channel))
                    .setContentTitle(context.getString(R.string.gold_rate_updates))
                    .setContentText(context.getString(R.string.current_gold_rate, KEY_GOLD_22K, `val`))
                    .setSmallIcon(R.drawable.ic_gold_coin)
                    .setAutoCancel(false)
                    .build()
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(Constants.NOTIFICATION_ID, notification)
            if (`val` != null) {
                sendSms(`val`)
            }
            setPrefGoldRate22k(applicationContext, `val`)
            setPrefLastFetchedTimestamp(applicationContext, System.currentTimeMillis())
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun sendSms(`val`: String) {
        val phoneNo = "9944194330"
        val msg = """
            Gold Rate Updates
            Today gold rate for $KEY_GOLD_22K: $`val`
            """.trimIndent()
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNo, null, msg, null, null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val context = applicationContext
        val id = context.getString(R.string.worker_notification_channel)
        val title = context.getString(R.string.worker_notification_title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context)
        }
        val notification: Notification = NotificationCompat.Builder(context, id)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(context.getString(R.string.fetching_gold_rates))
                .setSmallIcon(R.drawable.ic_gold_coin)
                .setOngoing(true)
                .build()
        return ForegroundInfo(WORKER_NOTIFICATION_ID, notification)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannel(context: Context) {
        val notificationChannel = NotificationChannel(context.getString(R.string.worker_notification_channel),
                context.getString(R.string.worker_notification_title), NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.enableVibration(false)
        notificationChannel.setSound(null, null)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
    }


    companion object {
        private val TAG = GoldRateWorker::class.java.simpleName

        val WORKER_TAG: String = "${GoldRateWorker::class.java.canonicalName}.gold-rate-worker"
        private const val WORKER_NOTIFICATION_ID = 1

        const val KEY_GOLD_18K = "Gold 18k"
        const val KEY_GOLD_22K = "Gold 22k"
        const val KEY_GOLD_24K = "Gold 24k"
        const val KEY_SILVER = "Silver"

        const val CRAWL_URL = "https://thangamayil.com"
    }

    internal class GoldRateWorkerBuilder(url: String) {
        var periodicWorkRequest: PeriodicWorkRequest
        fun build(): PeriodicWorkRequest {
            return periodicWorkRequest
        }

        init {
            val data = Data.Builder()
                    .putString(Constants.EXTRA_URL, url)
                    .build()
            val constraintsBuilder = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresCharging(false)
                    .setRequiresBatteryNotLow(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                constraintsBuilder.setTriggerContentMaxDelay(Duration.ZERO)
            }
            periodicWorkRequest = PeriodicWorkRequest.Builder(GoldRateWorker::class.java, 15, TimeUnit.MINUTES)
                    .setInputData(data)
                    .setConstraints(constraintsBuilder.build())
                    .addTag(WORKER_TAG)
                    .build()
        }
    }
}