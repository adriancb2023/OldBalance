package com.adriancruz.oldbalance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriancruz.oldbalance.data.WeightEntry
import com.adriancruz.oldbalance.data.WeightGoal
import com.adriancruz.oldbalance.data.WeightRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiEvent {
    object EntryAdded : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
}

class MainViewModel(private val repo: WeightRepository) : ViewModel() {
    val entries: StateFlow<List<WeightEntry>> =
        repo.allEntriesFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val goals: StateFlow<List<WeightGoal>> =
        repo.allGoalsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val totalWeightLoss: StateFlow<Float> =
        entries.map { entries ->
            if (entries.size < 2) 0f
            else entries.first().weightKg - entries.last().weightKg
        }.stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val progressTowardsGoal: StateFlow<Float> =
        entries.combine(goals) { entries, goals ->
            if (entries.isEmpty() || goals.isEmpty()) 0f
            else entries.last().weightKg - goals.first().targetKg
        }.stateIn(viewModelScope, SharingStarted.Eagerly, 0f)


    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

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

    fun addGoal(goal: WeightGoal) {
        viewModelScope.launch {
            val lastEntry = entries.value.lastOrNull()
            repo.addOrUpdateGoal(goal.copy(initialWeight = lastEntry?.weightKg))
        }
    }
}
