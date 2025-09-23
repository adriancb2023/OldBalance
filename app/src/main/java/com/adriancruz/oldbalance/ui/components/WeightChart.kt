package com.adriancruz.oldbalance.ui.components

import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.adriancruz.oldbalance.data.WeightEntry
import com.adriancruz.oldbalance.data.WeightGoal
import com.adriancruz.oldbalance.ui.theme.PrimaryBlue
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.SimpleDateFormat
import java.util.*

enum class ChartRange {
    WEEK, MONTH, ALL
}

@Composable
fun WeightChart(
    modifier: Modifier = Modifier,
    entries: List<WeightEntry>,
    goals: List<WeightGoal>
) {
    // Convierte el Color de Compose a ARGB de Android (Int)
    val primaryColor = PrimaryBlue.toArgb()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)

                legend.isEnabled = true

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.valueFormatter = object : ValueFormatter() {
                    private val format = SimpleDateFormat("dd MMM", Locale.getDefault())
                    override fun getFormattedValue(value: Float): String {
                        return format.format(Date(value.toLong()))
                    }
                }

                axisRight.isEnabled = false
                axisLeft.setDrawGridLines(false)
            }
        },
        update = { chart ->
            // Si no hay datos, limpiamos el gráfico
            if (entries.isEmpty()) {
                chart.clear()
                chart.invalidate()
                return@AndroidView
            }

            // Mapear entradas a puntos (x = fecha en millis como Float, y = peso)
            val points: List<Entry> = entries.map { entry ->
                Entry(entry.date.toFloat(), entry.weightKg.toFloat())
            }

            val set = LineDataSet(points, "Peso").apply {
                color = primaryColor
                lineWidth = 2f
                setDrawCircles(true)
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                circleColors = listOf(primaryColor)
                circleRadius = 4f
            }

            val dataSets = mutableListOf<ILineDataSet>()
            dataSets.add(set)

            // Añadir líneas de meta (goals)
            goals.forEach { goal ->
                val lastX = entries.lastOrNull()?.date?.toFloat() ?: System.currentTimeMillis().toFloat()
                val firstX = entries.firstOrNull()?.date?.toFloat() ?: (lastX - 7 * 24 * 3600 * 1000f)
                val linePoints = listOf(
                    Entry(firstX, goal.targetKg.toFloat()),
                    Entry(lastX, goal.targetKg.toFloat())
                )
                val gSet = LineDataSet(linePoints, "Meta ${goal.targetKg}kg").apply {
                    // Color hex defensivo: si falla, usar gris
                    this.color = try {
                        Color.parseColor(goal.colorHex)
                    } catch (e: Exception) {
                        Color.GRAY
                    }
                    lineWidth = 1.5f
                    setDrawCircles(false)
                    enableDashedLine(10f, 5f, 0f)
                    setDrawValues(false)
                }
                dataSets.add(gSet)
            }

            chart.data = LineData(dataSets)
            // Protecciones adicionales: setAxisMinimum / setAxisMaximum para asegurar compatibilidad
            chart.xAxis.setAxisMinimum(entries.first().date.toFloat())
            chart.xAxis.setAxisMaximum(entries.last().date.toFloat())

            chart.notifyDataSetChanged()
            chart.invalidate()
            chart.animateX(700)
        }
    )
}
