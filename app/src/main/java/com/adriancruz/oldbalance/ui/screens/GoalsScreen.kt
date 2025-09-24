package com.adriancruz.oldbalance.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adriancruz.oldbalance.data.WeightEntry
import com.adriancruz.oldbalance.data.WeightGoal
import com.adriancruz.oldbalance.ui.components.Loading
import com.adriancruz.oldbalance.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoalsScreen(viewModel: MainViewModel) {
    val goals by viewModel.goals.collectAsState()
    val entries by viewModel.entries.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableState of(true) }

    LaunchedEffect(key1 = Unit) {
        viewModel.goals.first()
        isLoading = false
    }

    Scaffold(
        floatingActionButton = {
            if (entries.isNotEmpty()) {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir objetivo")
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Loading()
        } else if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding), contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No hay objetivos definidos.")
                    if (entries.isEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Añade tu primer peso para poder crear un objetivo.")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(goals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        entries = entries,
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }

        if (showDialog) {
            val lastWeight = entries.lastOrNull()?.weightKg
            if (lastWeight != null) {
                AddGoalDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { targetKg ->
                        viewModel.addGoal(
                            WeightGoal(
                                targetKg = targetKg,
                                initialWeight = lastWeight
                            )
                        )
                        showDialog = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GoalCard(
    goal: WeightGoal,
    entries: List<WeightEntry>,
    modifier: Modifier = Modifier
) {
    val cardAlpha = if (goal.isActive) 1f else 0.6f

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (goal.isActive) "Meta Actual" else "Objetivo Anterior",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface.copy(alpha = cardAlpha)
                )
                Chip(
                    onClick = {},
                    enabled = false,
                ) {
                    Text(
                        text = if (goal.isActive) "Activo" else "Completado",
                        color = if (goal.isActive) MaterialTheme.colors.secondary else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${goal.targetKg} kg",
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.onSurface.copy(alpha = cardAlpha)
            )

            Spacer(modifier = Modifier.height(4.dp))

            val date = Instant.ofEpochMilli(goal.startDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            Text(
                text = "Iniciada: ${date.format(DateTimeFormatter.ofPattern("dd/MM/yy"))}",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = cardAlpha)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (goal.isActive) {
                val progressData = calculateProgress(goal, entries)
                if (progressData != null) {
                    val animatedProgress = remember { Animatable(0f) }
                    LaunchedEffect(progressData.progress) {
                        animatedProgress.animateTo(
                            targetValue = progressData.progress,
                            animationSpec = tween(durationMillis = 1000)
                        )
                    }

                    LinearProgressIndicator(
                        progress = animatedProgress.value,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(animatedProgress.value * 100).roundToInt()}% completado",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = cardAlpha)
                    )
                } else {
                    Text(
                        text = "Datos insuficientes para calcular el progreso.",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = cardAlpha)
                    )
                }
            } else {
                val totalRecords = entries.count { it.date >= goal.startDate && (goal.endDate == null || it.date <= goal.endDate) }
                Text(
                    text = "Total de registros: $totalRecords",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = cardAlpha)
                )
            }
        }
    }
}

private data class ProgressData(val progress: Float, val percentage: Float)

private fun calculateProgress(goal: WeightGoal, entries: List<WeightEntry>): ProgressData? {
    val initialWeight = goal.initialWeight ?: return null
    val lastWeight = entries.lastOrNull()?.weightKg ?: return null
    val targetWeight = goal.targetKg

    val totalSpan = initialWeight - targetWeight
    if (totalSpan == 0f) return ProgressData(1f, 100f)

    val currentProgress = initialWeight - lastWeight
    val progress = (currentProgress / totalSpan).coerceIn(0f, 1f)

    return ProgressData(progress, progress * 100)
}

@Composable
private fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Objetivo de Peso") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { new ->
                    if (new.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$")) || new.isEmpty()) {
                        text = new
                    }
                },
                label = { Text("Peso objetivo (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val kg = text.toFloatOrNull()
                    if (kg != null) {
                        onConfirm(kg)
                    }
                },
                enabled = text.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
