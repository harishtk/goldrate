package com.harishtk.goldrate.app.ui.screens.history

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.harishtk.goldrate.app.SIMPLE_DATE_TIME_PATTERN
import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import com.harishtk.goldrate.app.data.repository.MockGoldRateRepository
import com.harishtk.goldrate.app.ui.theme.GoldRateTheme
import com.harishtk.goldrate.app.ui.theme.transparentGray
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GoldRateTheme {
                Surface(color = MaterialTheme.colors.primary) {
                    HistoryScreen(LocalContext.current, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(context: Context, viewModel: HistoryViewModel) {
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
        content = { HistoryList(pagedEntries = viewModel.pagedGoldRateEntries) }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun HistoryList(pagedEntries: Flow<PagingData<GoldrateEntry>>) {
    val lazyGoldRateItems: LazyPagingItems<GoldrateEntry> = pagedEntries.collectAsLazyPagingItems()

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(lazyGoldRateItems, key = { it.timestamp }) { entry ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically() + expandVertically(expandFrom = Alignment.Bottom)
            ) {
            }
            HistoryRow(entry = entry!!)
        }

        lazyGoldRateItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    // Initial loading, show full screen loader indictor
                    item {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Loading..",
                                style = MaterialTheme.typography.subtitle2,
                                modifier = Modifier.padding(16.dp)
                            )
                            CircularProgressIndicator(color = MaterialTheme.colors.secondary)
                        }
                    }
                }
                loadState.append is LoadState.Loading -> {
                    // Load more is loading
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Loading items...")
                            CircularProgressIndicator(color = MaterialTheme.colors.secondary)
                        }
                    }
                }
                loadState.refresh is LoadState.Error -> {
                    val e = lazyGoldRateItems.loadState.refresh as LoadState.Error
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillParentMaxSize()
                        ) {
                            Text(
                                text = e.error.localizedMessage!!,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(
                                onClick = { retry() },
                                shape = RoundedCornerShape(15.dp, 20.dp, 35.dp, 35.dp),
                                modifier = Modifier
                                    .padding(16.dp)
                                    .background(color = MaterialTheme.colors.secondary)
                            ) {
                                Text(text = "RETRY", modifier = Modifier.padding(12.dp))
                            }
                        }
                    }
                }
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
                color = transparentGray,
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
        Modifier
            .fillMaxSize()
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

@Preview(showBackground = true)
@Composable
fun HistoryListPreview() {
    val repo = MockGoldRateRepository()
    // HistoryScreen(context = LocalContext.current, repo.getGoldrateEntries().value!!)
    GoldRateTheme {
         HistoryList(pagedEntries = repo.getGoldrateEntries())
    }
}