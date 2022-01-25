package com.harishtk.goldrate.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goldrate_entry")
data class GoldrateEntry(
    val timestamp: Long,
    val type: String,
    val price: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}