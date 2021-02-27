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
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.node.LayoutNode
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.harishtk.goldrate.app.ui.theme.GoldRateTheme
import com.harishtk.goldrate.app.work.GoldRateWorker

class MainActivity : AppCompatActivity() {
    private lateinit var msg: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        msg = "Loading.."

        setContent {
            GoldRateTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(getString(R.string.app_name), msg)
                }
            }
        }

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
        msg = getString(R.string.note)
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

@Composable
fun MainScreen(title: String, msg: String) {
    Scaffold(
            topBar = { TopAppBar(title = {Text(title)})  },
            bodyContent = {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                ) {
                    Column(
                            modifier = Modifier.padding(16.dp),
                    ) {
                        Text(text = msg, textAlign = TextAlign.Center)
                    }
                }
            }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoldRateTheme {
        MainScreen("Title", "Compose works!")
    }
}