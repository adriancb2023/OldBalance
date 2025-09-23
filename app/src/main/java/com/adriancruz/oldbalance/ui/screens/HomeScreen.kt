package com.adriancruz.oldbalance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adriancruz.oldbalance.data.WeightEntry
import com.adriancruz.oldbalance.ui.components.ChartRange
import com.adriancruz.oldbalance.ui.components.WeightChart
import com.adriancruz.oldbalance.ui.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.ZoneId


@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val entries by viewModel.entries.collectAsState()
    val goals by viewModel.goals.collectAsState()
    var selectedRange by remember { mutableStateOf(ChartRange.ALL) }

    val filteredEntries = remember(entries, selectedRange) {
        filterEntriesByRange(entries, selectedRange)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Tu Progreso", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(8.dp))

        if (entries.isNotEmpty()) {
            val latestWeight = entries.last().weightKg
            Text("Último peso: $latestWeight kg", style = MaterialTheme.typography.h6)
        } else {
            Text("Registra tu primer peso para ver la gráfica.", style = MaterialTheme.typography.body1)
        }


        Spacer(modifier = Modifier.height(16.dp))

        if (filteredEntries.isNotEmpty()) {
            ChartRangeSelector(
                selectedRange = selectedRange,
                onRangeSelected = { selectedRange = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            WeightChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                entries = filteredEntries,
                goals = goals
            )
        } else {
             Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                Text("No hay datos para este período.")
            }
        }
    }
}

@Composable
private fun ChartRangeSelector(
    selectedRange: ChartRange,
    onRangeSelected: (ChartRange) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = { onRangeSelected(ChartRange.WEEK) }) {
            Text("Semana", color = if (selectedRange == ChartRange.WEEK) MaterialTheme.colors.primary else LocalContentColor.current.copy(alpha = ContentAlpha.medium))
        }
        TextButton(onClick = { onRangeSelected(ChartRange.MONTH) }) {
            Text("Mes", color = if (selectedRange == ChartRange.MONTH) MaterialTheme.colors.primary else LocalContentColor.current.copy(alpha = ContentAlpha.medium))
        }
        TextButton(onClick = { onRangeSelected(ChartRange.ALL) }) {
            Text("Todo", color = if (selectedRange == ChartRange.ALL) MaterialTheme.colors.primary else LocalContentColor.current.copy(alpha = ContentAlpha.medium))
        }
    }
}

private fun filterEntriesByRange(entries: List<WeightEntry>, range: ChartRange): List<WeightEntry> {
    if (entries.size < 2) return entries

    val now = LocalDate.now()
    val limit = when (range) {
        ChartRange.WEEK -> now.minusWeeks(1)
        ChartRange.MONTH -> now.minusMonths(1)
        ChartRange.ALL -> return entries
    }
    val limitEpoch = limit.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return entries.filter { it.date >= limitEpoch }
}
