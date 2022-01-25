package com.harishtk.goldrate.app.ui.screens.history

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harishtk.goldrate.app.SIMPLE_DATE_TIME_PATTERN
import com.harishtk.goldrate.app.data.Resource
import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import com.harishtk.goldrate.app.ui.theme.GoldRateTheme
import com.harishtk.goldrate.app.ui.theme.yellow700
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GoldRateTheme {
                Surface(color = MaterialTheme.colors.surface) {
                    HistoryScreen(LocalContext.current, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(context: Context, viewModel: HistoryViewModel) {
    val entries by viewModel.goldrateEntries.observeAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "History") },
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            when (entries?.status) {
                Resource.Status.SUCCESS ->
                    if (entries?.data != null) {
                        HistoryList(entries = entries?.data!!)
                    } else {
                        Text("No data")
                    }
                Resource.Status.ERROR -> Text("No data: ${entries?.message}")
                Resource.Status.LOADING -> Loading()
                else -> Text(text = "Failed to fetch")
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryList(entries: List<GoldrateEntry>) {
    // TODO: This ideally would be done in the ViewModel
    val grouped = entries.groupBy { it.type }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        grouped.forEach { (title, data) ->
            stickyHeader {
                HistoryHeader(title = title)
            }
            items(data) { entry ->
                HistoryRow(entry = entry)
            }
        }
        item {
            Box(
                Modifier.height(60.dp)
            )
        }
    }
}

@Composable
private fun HistoryRow(entry: GoldrateEntry) {
    Row(
        modifier = Modifier
            .background(
                color = Color(0x80CCCCCC),
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 8.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                val time = SimpleDateFormat(SIMPLE_DATE_TIME_PATTERN).format(Date(entry.timestamp))
                Text(text = time)
            }
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = entry.price, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun HistoryHeader(title: String) {
    Row(
        Modifier.fillMaxSize()
            .background(Color.LightGray)
            .padding(4.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun Loading() {
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
            Text(text = "Loading.. Please wait", textAlign = TextAlign.Center)
        }
    }
}