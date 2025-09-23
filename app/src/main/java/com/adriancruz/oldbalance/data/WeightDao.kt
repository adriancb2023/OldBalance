package com.adriancruz.oldbalance.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: WeightEntry): Long

    @Update
    suspend fun updateEntry(entry: WeightEntry)

    @Delete
    suspend fun deleteEntry(entry: WeightEntry)

    @Query("SELECT * FROM weight_entries ORDER BY date ASC")
    fun getAllEntriesFlow(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries WHERE date BETWEEN :from AND :to ORDER BY date ASC")
    suspend fun getEntriesBetween(from: Long, to: Long): List<WeightEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: WeightGoal): Long

    @Update
    suspend fun updateGoal(goal: WeightGoal)

    @Query("SELECT * FROM weight_goals ORDER BY isActive DESC, startDate DESC")
    fun getAllGoalsFlow(): Flow<List<WeightGoal>>
}
