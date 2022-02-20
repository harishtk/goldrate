package com.harishtk.goldrate.app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.PagingData
import com.harishtk.goldrate.app.data.Resource
import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import com.harishtk.goldrate.app.data.repository.GoldRateRepositoryI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockGoldRateRepository : GoldRateRepositoryI {

    private val SERVICE_LATENCY_IN_MILLIS = 2000L

    private var GOLD_RATE_ENTRIES = mutableListOf<GoldrateEntry>()

    init {
        GOLD_RATE_ENTRIES.add(GoldrateEntry(System.currentTimeMillis(), "Gold: 22k", "$32.20"))
        GOLD_RATE_ENTRIES.add(GoldrateEntry(System.currentTimeMillis(), "Gold: 22k", "$32.20"))
        GOLD_RATE_ENTRIES.add(GoldrateEntry(System.currentTimeMillis(), "Gold: 22k", "$32.20"))
        GOLD_RATE_ENTRIES.add(GoldrateEntry(System.currentTimeMillis(), "Gold: 22k", "$32.20"))
        GOLD_RATE_ENTRIES.add(GoldrateEntry(System.currentTimeMillis(), "Gold: 22k", "$32.20"))
        GOLD_RATE_ENTRIES.add(GoldrateEntry(System.currentTimeMillis(), "Gold: 22k", "$32.20"))
        GOLD_RATE_ENTRIES.addAll(GOLD_RATE_ENTRIES)
        GOLD_RATE_ENTRIES.addAll(GOLD_RATE_ENTRIES)
        GOLD_RATE_ENTRIES.addAll(GOLD_RATE_ENTRIES)
    }

    fun getEntries(): Map<String, List<GoldrateEntry>> = GOLD_RATE_ENTRIES.groupBy { it.type }

    override fun getGoldrateEntries(): Flow<PagingData<GoldrateEntry>> =
        flow { emit(PagingData.from(GOLD_RATE_ENTRIES)) }
        /*liveData(Dispatchers.IO) {

            delay(SERVICE_LATENCY_IN_MILLIS)
            emit(Resource.success(GOLD_RATE_ENTRIES))
    }*/

    override fun getLastGoldrateEntry(): Flow<GoldrateEntry?> {
        return flow { emit(null) }
    }

    override suspend fun addEntry(goldrateEntry: GoldrateEntry): Long {
        return if (GOLD_RATE_ENTRIES.add(goldrateEntry)) 1 else 0
    }

}