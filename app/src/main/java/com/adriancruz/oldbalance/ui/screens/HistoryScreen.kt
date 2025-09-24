package com.adriancruz.oldbalance.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adriancruz.oldbalance.data.WeightEntry
import com.adriancruz.oldbalance.ui.components.Loading
import com.adriancruz.oldbalance.ui.theme.AppColors
import com.adriancruz.oldbalance.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(viewModel: MainViewModel) {
    val entries by viewModel.entries.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        viewModel.entries.first()
        isLoading = false
    }

    if (isLoading) {
        Loading()
    } else if (entries.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay registros todavía.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp)
        ) {
            itemsIndexed(entries, key = { _, item -> item.id }) { index, entry ->
                HistoryItem(
                    entry = entry,
                    previousEntry = entries.getOrNull(index + 1),
                    isLastItem = index == entries.lastIndex,
                    onDelete = { viewModel.removeEntry(entry) },
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(durationMillis = 500)
                    )
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    entry: WeightEntry,
    previousEntry: WeightEntry?,
    isLastItem: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Timeline(isLastItem = isLastItem)
        Spacer(modifier = Modifier.width(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 0.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    val date = Instant.ofEpochMilli(entry.date)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                        style = MaterialTheme.typography.body2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "%.1f kg".format(entry.weightKg),
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    WeightDifference(entry, previousEntry)
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar registro"
                    )
                }
            }
        }
    }
}

@Composable
fun Timeline(isLastItem: Boolean) {
    val timelineColor = MaterialTheme.colors.primary
    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
        Canvas(modifier = Modifier.fillMaxHeight()) {
            drawIntoCanvas {
                val circleRadius = 8.dp.toPx()
                val circleCenter = Offset(x = circleRadius, y = circleRadius + 8.dp.toPx())

                drawCircle(
                    color = timelineColor,
                    radius = circleRadius,
                    center = circleCenter
                )

                if (!isLastItem) {
                    drawLine(
                        color = timelineColor,
                        start = Offset(x = circleRadius, y = circleCenter.y + circleRadius),
                        end = Offset(x = circleRadius, y = size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }
    }
}

@Composable
fun WeightDifference(entry: WeightEntry, previousEntry: WeightEntry?) {
    val difference = previousEntry?.let { entry.weightKg - it.weightKg }

    Row(verticalAlignment = Alignment.CenterVertically) {
        when {
            difference == null -> {
                Text(text = "— Inicio", color = Color.Gray, fontSize = 14.sp)
            }
            difference < 0 -> {
                Text(text = "↓ %.1f kg".format(abs(difference)), color = AppColors.SuccessGreen, fontSize = 14.sp)
            }
            difference > 0 -> {
                Text(text = "↑ %.1f kg".format(abs(difference)), color = AppColors.AlertRed, fontSize = 14.sp)
            }
            else -> {
                Text(text = "— Manteniendo", color = AppColors.ProgressOrange, fontSize = 14.sp)
            }
        }
    }
}
