package com.harishtk.goldrate.app.data.repository

import androidx.lifecycle.LiveData
import com.harishtk.goldrate.app.data.Resource
import com.harishtk.goldrate.app.data.entities.GoldrateEntry

interface GoldRateRepositoryI {
    fun getGoldrateEntries(): LiveData<Resource<List<GoldrateEntry>>>

    suspend fun addEntry(goldrateEntry: GoldrateEntry): Long
}