package com.harishtk.goldrate.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.harishtk.goldrate.app.databinding.ActivityMainBinding
import com.harishtk.goldrate.app.work.GoldRateWorker

class MainActivity: AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)

        checkSmsPermission()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(this, getString(R.string.notification_channel), getString(R.string.notification_title))
        }
        checkScheduledWorker()
    }

    private fun checkScheduledWorker() {
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(GoldRateWorker.WORKER_TAG,
                        ExistingPeriodicWorkPolicy.KEEP,
                        GoldRateWorker.GoldRateWorkerBuilder(CRAWL_URL).build())
        viewBinding.textView.setText(R.string.note)
    }

    private fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 200)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannel(@NonNull context: Context, @NonNull channelId: String, @NonNull title: String) {
        val notificationChannel = NotificationChannel(channelId,
                title, NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.enableVibration(false)
        notificationChannel.setSound(null, null)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
    }

    companion object {
        const val CRAWL_URL = "https://thangamayil.com"
    }
}