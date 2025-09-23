package com.adriancruz.oldbalance.data

class WeightRepository(private val dao: WeightDao) {
    val allEntriesFlow = dao.getAllEntriesFlow()
    val activeGoalsFlow = dao.getActiveGoalsFlow()

    suspend fun addOrUpdateEntry(entry: WeightEntry) {
        if (entry.id == 0L) dao.insertEntry(entry) else dao.updateEntry(entry)
    }
    suspend fun deleteEntry(entry: WeightEntry) = dao.deleteEntry(entry)

    suspend fun addOrUpdateGoal(goal: WeightGoal) {
        if (goal.id == 0L) dao.insertGoal(goal) else dao.updateGoal(goal)
    }
}
