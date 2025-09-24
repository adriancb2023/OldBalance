package com.adriancruz.oldbalance.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Flag
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adriancruz.oldbalance.data.WeightEntry
import com.adriancruz.oldbalance.data.WeightGoal
import com.adriancruz.oldbalance.ui.components.ChartRange
import com.adriancruz.oldbalance.ui.components.Loading
import com.adriancruz.oldbalance.ui.components.WeightChart
import com.adriancruz.oldbalance.ui.theme.AppColors
import com.adriancruz.oldbalance.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val entries by viewModel.entries.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val totalWeightLoss by viewModel.totalWeightLoss.collectAsState()
    val progressTowardsGoal by viewModel.progressTowardsGoal.collectAsState()
    var selectedRange by remember { mutableStateOf(ChartRange.ALL) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        // Esperamos a la primera emisión para saber que la carga inicial ha terminado
        viewModel.entries.first()
        isLoading = false
    }

    val filteredEntries = remember(entries, selectedRange) {
        filterEntriesByRange(entries, selectedRange)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF0F0F0)
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
            }
        ) { loading ->
            if (loading) {
                Loading()
            } else {
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
                        Text(
                            "Registra tu primer peso para ver la gráfica.",
                            style = MaterialTheme.typography.body1
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (filteredEntries.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Gráfico de Peso", style = MaterialTheme.typography.h6)
                                    ChartRangeSelector(
                                        selectedRange = selectedRange,
                                        onRangeSelected = { selectedRange = it }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                WeightChart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    entries = filteredEntries,
                                    goals = goals
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                if (goals.isNotEmpty()) {
                                    ChartLegend(goal = goals.first())
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No hay datos para este período.")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedVisibility(
                        visible = !isLoading,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 500, delayMillis = 300)
                        ) + fadeIn(animationSpec = tween(500, delayMillis = 300))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            SummaryCard(
                                title = "Pérdida Total",
                                value = "%.1f kg".format(totalWeightLoss),
                                icon = Icons.Default.ArrowDownward,
                                color = AppColors.SuccessGreen,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            SummaryCard(
                                title = "Hacia la Meta",
                                value = "%.1f kg".format(progressTowardsGoal),
                                icon = Icons.Default.Flag,
                                color = AppColors.ProgressOrange,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.subtitle1)
            Text(text = value, style = MaterialTheme.typography.h5, color = color)
        }
    }
}

@Composable
private fun ChartRangeSelector(
    selectedRange: ChartRange,
    onRangeSelected: (ChartRange) -> Unit
) {
    Row {
        ChartRangeButton(
            text = "Semana",
            isSelected = selectedRange == ChartRange.WEEK,
            onClick = { onRangeSelected(ChartRange.WEEK) }
        )
        ChartRangeButton(
            text = "Mes",
            isSelected = selectedRange == ChartRange.MONTH,
            onClick = { onRangeSelected(ChartRange.MONTH) }
        )
        ChartRangeButton(
            text = "Todo",
            isSelected = selectedRange == ChartRange.ALL,
            onClick = { onRangeSelected(ChartRange.ALL) }
        )
    }
}

@Composable
private fun ChartRangeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) MaterialTheme.colors.primary else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary
        ),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp)
    ) {
        Text(text)
    }
}

@Composable
private fun ChartLegend(goal: WeightGoal) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(color = AppColors.PrimaryBlue, text = "Peso")
        Spacer(modifier = Modifier.width(16.dp))
        LegendItem(color = AppColors.ProgressOrange, text = "Meta ${goal.targetKg}kg")
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.body2)
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
