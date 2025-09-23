package com.adriancruz.oldbalance

import android.app.Application
import androidx.room.Room
import com.adriancruz.oldbalance.data.AppDatabase
import com.adriancruz.oldbalance.data.WeightRepository

class WeightApp : Application() {

    // Using by lazy so the database and repository are only created when they're needed
    // for the first time.
    val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "weight_tracker_db"
        ).build()
    }

    val repository by lazy {
        WeightRepository(database.weightDao())
    }
}
