package com.adriancruz.oldbalance.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de acceso a datos (DAO) para las entidades de peso.
 *
 * Proporciona metodos para interactuar con la base de datos.
 */
@Dao
interface WeightDao {
    /**
     * Inserta un nuevo registro de peso en la base de datos.
     *
     * @param entry el registro de peso a insertar.
     * @return el ID del registro insertado.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: WeightEntry): Long

    /**
     * Actualiza un registro de peso existente.
     *
     * @param entry el registro de peso a actualizar.
     */
    @Update
    suspend fun updateEntry(entry: WeightEntry)

    /**
     * Elimina un registro de peso.
     *
     * @param entry el registro de peso a eliminar.
     */
    @Delete
    suspend fun deleteEntry(entry: WeightEntry)

    /**
     * Elimina todos los registros de peso de la base de datos.
     */
    @Query("DELETE FROM weight_entries")
    suspend fun deleteAllEntries()

    /**
     * Obtiene todos los registros de peso como un [Flow].
     *
     * @return un [Flow] que emite la lista de todos los registros de peso.
     */
    @Query("SELECT * FROM weight_entries ORDER BY date ASC")
    fun getAllEntriesFlow(): Flow<List<WeightEntry>>

    /**
     * Obtiene los registros de peso en un rango de fechas.
     *
     * @param from fecha de inicio del rango.
     * @param to fecha de fin del rango.
     * @return una lista de registros de peso en el rango especificado.
     */
    @Query("SELECT * FROM weight_entries WHERE date BETWEEN :from AND :to ORDER BY date ASC")
    suspend fun getEntriesBetween(from: Long, to: Long): List<WeightEntry>

    /**
     * Inserta un nuevo objetivo de peso.
     *
     * @param goal el objetivo de peso a insertar.
     * @return el ID del objetivo insertado.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: WeightGoal): Long

    /**
     * Actualiza un objetivo de peso existente.
     *
     * @param goal el objetivo de peso a actualizar.
     */
    @Update
    suspend fun updateGoal(goal: WeightGoal)

    /**
     * Elimina todos los objetivos de peso.
     */
    @Query("DELETE FROM weight_goals")
    suspend fun deleteAllGoals()

    /**
     * Obtiene todos los objetivos de peso como un [Flow].
     *
     * @return un [Flow] que emite la lista de todos los objetivos de peso.
     */
    @Query("SELECT * FROM weight_goals ORDER BY isActive DESC, startDate DESC")
    fun getAllGoalsFlow(): Flow<List<WeightGoal>>
}
