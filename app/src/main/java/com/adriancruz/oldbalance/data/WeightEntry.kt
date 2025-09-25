package com.adriancruz.oldbalance.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "weight_entries",
        indices = [Index(value = ["date"], unique = true)])
data class WeightEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val weightKg: Float,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
