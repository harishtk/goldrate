package com.harishtk.goldrate.app.di

import android.content.res.Resources
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AndroidResourceEntryPoint {
    fun appResource(): Resources
}