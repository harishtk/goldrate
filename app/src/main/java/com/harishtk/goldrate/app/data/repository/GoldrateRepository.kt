package com.harishtk.goldrate.app.data.repository

import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import com.harishtk.goldrate.app.data.local.GoldrateEntryDao
import com.harishtk.goldrate.app.data.performGetOperation
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

class GoldrateRepository @Inject constructor(
    private val remoteDataSource: Any? = null,
    private val localDataSource: GoldrateEntryDao
) : GoldRateRepositoryI {

    override fun getGoldrateEntries() = performGetOperation(
        databaseQuery = { localDataSource.getAllEntries() }
    )

    override suspend fun addEntry(goldrateEntry: GoldrateEntry) =
        localDataSource.insert(goldrateEntry)
}