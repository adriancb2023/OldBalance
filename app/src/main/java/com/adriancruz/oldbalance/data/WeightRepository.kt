package com.adriancruz.oldbalance.data

/**
 * Repositorio para gestionar los datos de peso.
 *
 * @property dao el objeto de acceso a datos (DAO) para interactuar con la base de datos.
 */
class WeightRepository(private val dao: WeightDao) {
    /**
     * Un [Flow] que emite la lista de todos los registros de peso.
     */
    val allEntriesFlow = dao.getAllEntriesFlow()

    /**
     * Un [Flow] que emite la lista de todos los objetivos de peso.
     */
    val allGoalsFlow = dao.getAllGoalsFlow()

    /**
     * Anade o actualiza un registro de peso.
     *
     * @param entry el registro de peso a anadir o actualizar.
     */
    suspend fun addOrUpdateEntry(entry: WeightEntry) {
        if (entry.id == 0L) dao.insertEntry(entry) else dao.updateEntry(entry)
    }

    /**
     * Elimina un registro de peso.
     *
     * @param entry el registro de peso a eliminar.
     */
    suspend fun deleteEntry(entry: WeightEntry) = dao.deleteEntry(entry)

    /**
     * Elimina todos los registros de peso.
     */
    suspend fun deleteAllEntries() = dao.deleteAllEntries()

    /**
     * Anade o actualiza un objetivo de peso.
     *
     * @param goal el objetivo de peso a anadir o actualizar.
     */
    suspend fun addOrUpdateGoal(goal: WeightGoal) {
        if (goal.id == 0L) dao.insertGoal(goal) else dao.updateGoal(goal)
    }

    /**
     * Elimina todos los objetivos de peso.
     */
    suspend fun deleteAllGoals() = dao.deleteAllGoals()
}
