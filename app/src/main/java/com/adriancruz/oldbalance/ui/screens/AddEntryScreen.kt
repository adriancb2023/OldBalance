package com.adriancruz.oldbalance.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adriancruz.oldbalance.ui.components.StyledInputField
import com.adriancruz.oldbalance.ui.components.WeeklyProgressChart
import com.adriancruz.oldbalance.ui.theme.AppColors
import com.adriancruz.oldbalance.ui.theme.Typography
import com.adriancruz.oldbalance.ui.viewmodel.MainViewModel
import com.adriancruz.oldbalance.ui.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AddEntryScreen(viewModel: MainViewModel) {
    var weightText by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    val weeklyEntries by viewModel.weeklyEntries.collectAsState()
    val weeklyChange by viewModel.weeklyWeightChange.collectAsState()

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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "¿Cómo vamos hoy?",
                style = Typography.h4.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Registra tu peso para seguir tu progreso.",
                style = Typography.body1,
                color = AppColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Progress Chart Card
            Card(elevation = 0.dp, shape = com.adriancruz.oldbalance.ui.theme.Shapes.large) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Progreso (Últimos 7 días)",
                            style = Typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                        )
                        weeklyChange?.let {
                            val sign = if (it > 0) "+" else ""
                            val color = if (it > 0) AppColors.AlertRed else AppColors.SuccessGreen
                            Text(
                                text = "~ ${sign}${String.format("%.1f", it)} kg",
                                style = Typography.subtitle1.copy(color = color, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (weeklyEntries.isNotEmpty()) {
                        WeeklyProgressChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            entries = weeklyEntries
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay suficientes datos para mostrar el gráfico.")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Input Fields
            Text("Peso (kg)", style = Typography.subtitle2.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            StyledInputField(
                value = weightText,
                onValueChange = { new ->
                    if (new.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$")) || new.isEmpty()) {
                        weightText = new
                    }
                },
                placeholder = "Introduce tu peso",
                icon = Icons.Default.MonitorWeight,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val datePickerDialog = DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    date = LocalDate.of(year, month + 1, dayOfMonth)
                }, date.year, date.monthValue - 1, date.dayOfMonth
            )

            Text("Fecha", style = Typography.subtitle2.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            val formattedDate = remember(date) {
                val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", Locale("es", "ES"))
                date.format(formatter)
            }
            StyledInputField(
                value = formattedDate,
                onValueChange = {},
                placeholder = "",
                icon = Icons.Default.DateRange,
                readOnly = true,
                onClick = { datePickerDialog.show() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            val enabled = weightText.isNotBlank()
            Button(
                onClick = {
                    val kg = weightText.toFloatOrNull() ?: return@Button
                    val epoch = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    viewModel.addEntry(epoch, kg, null)
                },
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(AppColors.ButtonGradientStart, AppColors.ButtonGradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.NoteAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar Registro", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
