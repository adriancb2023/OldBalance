package com.adriancruz.oldbalance.ui.screens

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adriancruz.oldbalance.data.WeightGoal
import com.adriancruz.oldbalance.ui.viewmodel.MainViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun GoalsScreen(viewModel: MainViewModel) {
    val goals by viewModel.goals.collectAsState()
    val entries by viewModel.entries.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir objetivo")
            }
        }
    ) { padding ->
        if (goals.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay objetivos activos.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(goals, key = { it.id }) { goal ->
                    Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val date = Instant.ofEpochMilli(goal.startDate)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            Text(
                                "Meta: ${goal.targetKg} kg",
                                style = MaterialTheme.typography.h6
                            )
                            Text(
                                "Iniciada: ${date.format(DateTimeFormatter.ofPattern("dd/MM/yy"))}",
                                style = MaterialTheme.typography.caption
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val etaResult = viewModel.getEtaForGoal(goal, entries)
                            Text(
                                text = etaResult.message,
                                style = MaterialTheme.typography.body2,
                                color = if (etaResult.reachable) MaterialTheme.colors.secondary else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AddGoalDialog(
                onDismiss = { showDialog = false },
                onConfirm = { targetKg ->
                    viewModel.addGoal(WeightGoal(targetKg = targetKg))
                    showDialog = false
                }
            )
        }
    }
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
