package com.adriancruz.oldbalance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriancruz.oldbalance.data.WeightEntry
import com.adriancruz.oldbalance.data.WeightGoal
import com.adriancruz.oldbalance.data.WeightRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class UiEvent {
    object EntryAdded : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
}

class MainViewModel(private val repo: WeightRepository) : ViewModel() {

    // Todos los registros de peso
    val entries: StateFlow<List<WeightEntry>> =
        repo.allEntriesFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Goals activos (aquí consideramos activo si endDate == null)
    val goals: StateFlow<List<WeightGoal>> =
        repo.allGoalsFlow
            .map { list -> list.filter { it.isActive } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Últimos 7 días de entradas
    val weeklyEntries: StateFlow<List<WeightEntry>> =
        entries.map { it.takeLast(7) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Cambio de peso en la última semana
    val weeklyWeightChange: StateFlow<Float?> = entries.map { allEntries ->
        if (allEntries.size < 2) null
        else {
            val last7 = allEntries.takeLast(7)
            if (last7.size < 2) null
            else last7.last().weightKg - last7.first().weightKg
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Pérdida total de peso
    val totalWeightLoss: StateFlow<Float> = entries.map { list ->
        if (list.isEmpty()) 0f
        else list.first().weightKg - list.last().weightKg
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    // Progreso hacia el primer objetivo activo
    val progressTowardsGoal: StateFlow<Float> = combine(entries, goals) { entryList, goalList ->
        if (entryList.isEmpty() || goalList.isEmpty()) 0f
        else {
            val latestWeight = entryList.last().weightKg
            val targetWeight = goalList.first().targetKg
            targetWeight - latestWeight
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // Funciones CRUD para entradas
    fun addEntry(date: Long, kg: Float, note: String?) {
        viewModelScope.launch {
            repo.addOrUpdateEntry(WeightEntry(date = date, weightKg = kg, note = note))
            _uiEvent.emit(UiEvent.EntryAdded)
        }
    }

    fun updateEntry(entry: WeightEntry) {
        viewModelScope.launch { repo.addOrUpdateEntry(entry) }
    }

    fun removeEntry(entry: WeightEntry) {
        viewModelScope.launch { repo.deleteEntry(entry) }
    }

    // Funciones CRUD para goals
    fun addGoal(goal: WeightGoal) {
        viewModelScope.launch { repo.addOrUpdateGoal(goal) }
    }

    // Estimación de ETA para un goal
    fun getEtaForGoal(
        goal: WeightGoal,
        entries: List<WeightEntry>
    ): com.adriancruz.oldbalance.domain.EtaResult {
        return com.adriancruz.oldbalance.domain.estimateEta(entries, goal.targetKg)
    }

    // --- Developer Functions ---
    fun insertTestData() {
        viewModelScope.launch {
            deleteAllData() // Clear existing data first

            val calendar = Calendar.getInstance()

            // Insert weight entries
            val testEntries = mutableListOf<WeightEntry>()
            for (i in 0..10) {
                calendar.add(Calendar.DAY_OF_YEAR, -i)
                testEntries.add(
                    WeightEntry(
                        date = calendar.timeInMillis,
                        weightKg = 80f - i * 0.5f,
                        note = "Nota de prueba $i"
                    )
                )
            }
            testEntries.reversed().forEach { repo.addOrUpdateEntry(it) }
            _uiEvent.emit(UiEvent.ShowSnackbar("Test data inserted (11 entries)"))
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            repo.deleteAllEntries()
            repo.deleteAllGoals()
            _uiEvent.emit(UiEvent.ShowSnackbar("All data deleted"))
        }
    }
}
