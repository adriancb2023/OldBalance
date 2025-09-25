package com.adriancruz.oldbalance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_goals")
data class WeightGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetKg: Float,
    val startDate: Long = System.currentTimeMillis(),
    val initialWeight: Float? = null,
    val endDate: Long? = null,
    val colorHex: String = "#F59E0B",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
