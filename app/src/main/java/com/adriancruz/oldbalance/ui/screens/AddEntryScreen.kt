package com.adriancruz.oldbalance.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adriancruz.oldbalance.ui.viewmodel.MainViewModel
import com.adriancruz.oldbalance.ui.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun AddEntryScreen(viewModel: MainViewModel) {
    var weightText by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.EntryAdded -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "Peso registrado correctamente",
                        duration = SnackbarDuration.Short
                    )
                    weightText = ""
                }
                is UiEvent.ShowSnackbar -> {
                     scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Registrar peso", style = MaterialTheme.typography.h5)
            OutlinedTextField(
                value = weightText,
                onValueChange = { new ->
                    // permite solo números y punto
                    if (new.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$")) || new.isEmpty()) {
                        weightText = new
                    }
                },
                label = { Text("Peso (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Date Picker Dialog
            val datePickerDialog = DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    date = LocalDate.of(year, month + 1, dayOfMonth)
                }, date.year, date.monthValue - 1, date.dayOfMonth
            )

            TextButton(onClick = { datePickerDialog.show() }) {
                Text("Fecha: ${date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}")
            }

            val enabled = weightText.isNotBlank()
            val scale by animateFloatAsState(if (enabled) 1f else 0.98f)
            Button(
                onClick = {
                    val kg = weightText.toFloatOrNull() ?: return@Button
                    val epoch = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    viewModel.addEntry(epoch, kg, null)
                },
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(scaleX = scale, scaleY = scale)
            ) {
                Text("Guardar")
            }
        }
    }
}
