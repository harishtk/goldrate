package com.harishtk.goldrate.app.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.harishtk.goldrate.app.data.Resource
import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import kotlinx.coroutines.flow.Flow

interface GoldRateRepositoryI {
    fun getGoldrateEntries(): Flow<PagingData<GoldrateEntry>>

    fun getLastGoldrateEntry(): Flow<GoldrateEntry?>

    suspend fun addEntry(goldrateEntry: GoldrateEntry): Long
}