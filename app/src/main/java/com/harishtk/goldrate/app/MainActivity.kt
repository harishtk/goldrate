package com.harishtk.goldrate.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.*
import androidx.navigation.compose.ComposeNavigator
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.Operation
import androidx.work.WorkManager
import com.harishtk.goldrate.app.data.Resource
import com.harishtk.goldrate.app.data.repository.GoldrateRepository
import com.harishtk.goldrate.app.data.repository.MockGoldRateRepository
import com.harishtk.goldrate.app.ui.screens.history.HistoryActivity
import com.harishtk.goldrate.app.ui.theme.GoldRateTheme
import com.harishtk.goldrate.app.work.GoldRateWorker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var msg: String

    private val viewModel: MainViewModel by viewModels()

    private lateinit var historyLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        msg = "Loading.."

        setContent {
            GoldRateTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.surface) {
                    val context = LocalContext.current
                    MainScreen(context, getString(R.string.app_name), viewModel)
                }
            }
        }

        checkSmsPermission()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(
                this,
                getString(R.string.notification_channel),
                getString(R.string.notification_title)
            )
        }
        checkScheduledWorker()
    }

    private fun checkScheduledWorker() {
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                GoldRateWorker.WORKER_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                GoldRateWorker.GoldRateWorkerBuilder(CRAWL_URL).build()
            ).state.observe(this) { state ->
                msg = when (state) {
                    is Operation.State.SUCCESS -> getString(R.string.note)
                    else -> getString(R.string.fail_note)
                }
                viewModel.setMessage(msg)
            }
    }

    private fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 200)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannel(
        @NonNull context: Context,
        @NonNull channelId: String,
        @NonNull title: String
    ) {
        val notificationChannel = NotificationChannel(
            channelId,
            title, NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.enableVibration(false)
        notificationChannel.setSound(null, null)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
    }

    companion object {
        const val CRAWL_URL = "https://thangamayil.com"
    }
}

public fun NavGraphBuilder.composable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class], content).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
            deepLinks.forEach { deepLink ->
                addDeepLink(deepLink)
            }
        }
    )
}

@Composable
fun MainScreen(context: Context, title: String, mainViewModel: MainViewModel) {
    val uiState by mainViewModel.uiState.collectAsState()
    val msg by mainViewModel.msgFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                actions = {
                    IconButton(onClick = {
                        val historyIntent = Intent(context, HistoryActivity::class.java)
                        context.startActivity(historyIntent)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_history_24),
                            "History"
                        )
                    }
                },
                elevation = 0.dp
            )
        },
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = msg, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(60.dp))

                    when {
                        uiState.error != null -> {
                            Text(
                                text = "Failed to load data! ${uiState.error?.message}",
                                color = Color.Black
                            )
                        }
                        uiState.loading -> {
                            CircularProgressIndicator()
                        }
                        else -> {
                            val entry = uiState.lastGoldrateEntry!!
                            val time =
                                SimpleDateFormat(SIMPLE_DATE_TIME_PATTERN).format(Date(entry.timestamp))
                            Text(text = "Last: $time ${entry.type} ${entry.price}")
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    /*GoldRateTheme {
        MainScreen(LocalContext.current, "Title", "Compose works!")
    }*/

    GoldRateTheme {
        // HistoryList(entries = repo.getEntries())
        Column {
            Text("Preview")
        }
    }
}

const val SIMPLE_DATE_TIME_PATTERN = "hh:MM aa dd-MM-yyy"