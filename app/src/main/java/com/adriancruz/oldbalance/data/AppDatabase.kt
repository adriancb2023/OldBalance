package com.adriancruz.oldbalance.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Clase que representa la base de datos de la aplicacion.
 *
 * Utiliza Room para gestionar la base de datos SQLite.
 * @property entities lista de entidades que forman parte de la base de datos.
 * @property version numero de version de la base de datos.
 */
@Database(entities = [WeightEntry::class, WeightGoal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Devuelve una instancia del DAO para acceder a los datos de los pesos.
     *
     * @return instancia de [WeightDao].
     */
    abstract fun weightDao(): WeightDao
}
