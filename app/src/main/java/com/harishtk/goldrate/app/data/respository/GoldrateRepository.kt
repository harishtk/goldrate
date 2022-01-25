package com.harishtk.goldrate.app.data.respository

import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import com.harishtk.goldrate.app.data.local.GoldrateEntryDao
import com.harishtk.goldrate.app.data.performGetOperation
import javax.inject.Inject

class GoldrateRepository @Inject constructor(
    private val remoteDataSource: Any? = null,
    private val localDataSource: GoldrateEntryDao
) {

    fun getGoldrateEntries() = performGetOperation(
        databaseQuery = { localDataSource.getAllEntries() }
    )

    suspend fun addEntry(goldrateEntry: GoldrateEntry) =
        localDataSource.insert(goldrateEntry)
}