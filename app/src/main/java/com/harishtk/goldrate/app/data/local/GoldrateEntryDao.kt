package com.harishtk.goldrate.app.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.harishtk.goldrate.app.data.entities.GoldrateEntry

@Dao
interface GoldrateEntryDao {

    @Query("SELECT * FROM goldrate_entry ORDER BY timestamp DESC")
    fun getAllEntries() : LiveData<List<GoldrateEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goldrateEntries: List<GoldrateEntry>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goldrateEntry: GoldrateEntry): Long
}