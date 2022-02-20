package com.harishtk.goldrate.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.harishtk.goldrate.app.data.entities.GoldrateEntry
import com.harishtk.goldrate.app.data.local.DEFAULT_PAGE_SIZE
import com.harishtk.goldrate.app.data.local.GoldrateEntryDao
import com.harishtk.goldrate.app.data.performGetOperation
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoldrateRepository @Inject constructor(
    private val remoteDataSource: Any? = null,
    private val localDataSource: GoldrateEntryDao
) : GoldRateRepositoryI {

    override fun getGoldrateEntries(): Flow<PagingData<GoldrateEntry>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { localDataSource.getAllEntries() }
        ).flow
    }

    override fun getLastGoldrateEntry(): Flow<GoldrateEntry?> {
        return localDataSource.getLastEntry()
    }

    override suspend fun addEntry(goldrateEntry: GoldrateEntry) =
        localDataSource.insert(goldrateEntry)
}