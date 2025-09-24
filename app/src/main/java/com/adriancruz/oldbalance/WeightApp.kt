package com.adriancruz.oldbalance

import android.app.Application
import androidx.room.Room
import com.adriancruz.oldbalance.data.AppDatabase
import com.adriancruz.oldbalance.data.WeightRepository

class WeightApp : Application() {

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
