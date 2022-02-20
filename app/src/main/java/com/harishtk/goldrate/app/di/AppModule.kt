package com.harishtk.goldrate.app.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.harishtk.goldrate.app.data.local.AppDatabase
import com.harishtk.goldrate.app.data.local.GoldrateEntryDao
import com.harishtk.goldrate.app.data.repository.GoldrateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase = AppDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideGoldRateDao(db: AppDatabase) = db.goldrateEntryDao()

    @Singleton
    @Provides
    fun provideGoldRateRepo(localDataSource: GoldrateEntryDao) =
        GoldrateRepository(localDataSource = localDataSource)

    @Singleton
    @Provides
    fun provideAppResource(@ApplicationContext appContext: Context): Resources = appContext.resources
}