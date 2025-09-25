package com.adriancruz.oldbalance

import android.app.Application
import androidx.room.Room
import com.adriancruz.oldbalance.data.AppDatabase
import com.adriancruz.oldbalance.data.WeightRepository

/**
 * Clase principal de la aplicacion, inicializa la base de datos y el repositorio.
 */
class WeightApp : Application() {

    /**
     * Instancia de la base de datos de la aplicacion.
     *
     * Se inicializa de forma diferida (lazy) para asegurar que solo se cree cuando se necesite.
     */
    val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "weight_tracker_db"
        ).build()
    }

    /**
     * Instancia del repositorio de pesos.
     *
     * Proporciona acceso a los datos de la aplicacion, ya sea desde la base de datos o una fuente remota.
     */
    val repository by lazy {
        WeightRepository(database.weightDao())
    }
}
