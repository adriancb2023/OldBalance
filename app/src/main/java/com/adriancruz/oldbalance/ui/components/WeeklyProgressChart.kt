package com.adriancruz.oldbalance.ui.components

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.adriancruz.oldbalance.data.WeightEntry
import com.adriancruz.oldbalance.ui.theme.AppColors
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@Composable
fun WeeklyProgressChart(
    modifier: Modifier = Modifier,
    entries: List<WeightEntry>,
) {
    if (entries.isEmpty()) {
        return
    }

    val chartLineColor = AppColors.ChartLineColor.toArgb()
    val chartGradientTopColor = AppColors.ChartLineColor.copy(alpha = 0.4f).toArgb()
    val chartGradientBottomColor = Color.TRANSPARENT

    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(false)
                isDragEnabled = false
                setScaleEnabled(false)
                setPinchZoom(false)
                setDrawGridBackground(false)
                minOffset = 0f

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.setDrawAxisLine(false)
                xAxis.textColor = AppColors.TextSecondary.toArgb()
                xAxis.yOffset = 10f
                xAxis.valueFormatter = object : ValueFormatter() {
                    private val format = SimpleDateFormat("EEE", Locale("es", "ES"))
                    private val today = LocalDate.now()

                    override fun getFormattedValue(value: Float): String {
                        val date = Instant.ofEpochMilli(value.toLong())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        return if (date.isEqual(today)) {
                            "Hoy"
                        } else {
                            format.format(Date(value.toLong()))
                                .removeSuffix(".")
                                .replaceFirstChar { it.titlecase(Locale.getDefault()) }
                        }
                    }
                }

                axisLeft.isEnabled = false
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val points: List<Entry> = entries.map { entry ->
                Entry(entry.date.toFloat(), entry.weightKg.toFloat())
            }

            val set = LineDataSet(points, "Peso").apply {
                color = chartLineColor
                lineWidth = 2.5f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawValues(false)
                setDrawCircles(false)

                setDrawFilled(true)
                fillDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(chartGradientTopColor, chartGradientBottomColor)
                )
            }

            chart.data = LineData(set)
            chart.xAxis.labelCount = if (points.size > 1) points.size else 1
            chart.xAxis.axisMinimum = points.first().x - 1000
            chart.xAxis.axisMaximum = points.last().x + 1000

            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    )
}
