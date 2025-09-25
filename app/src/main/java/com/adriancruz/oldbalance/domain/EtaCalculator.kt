package com.adriancruz.oldbalance.domain

import com.adriancruz.oldbalance.data.WeightEntry
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class EtaResult(val reachable: Boolean, val estimatedDate: LocalDate?, val message: String)

fun estimateEta(entries: List<WeightEntry>, targetKg: Float): EtaResult {
    if (entries.size < 3) return EtaResult(false, null, "Datos insuficientes (mín. 3 registros).")

    val recentEntries = if (entries.size > 30) entries.takeLast(30) else entries
    if (recentEntries.size < 3) return EtaResult(
        false,
        null,
        "Datos insuficientes en el último mes."
    )


    val xs = recentEntries.map { it.date.toDouble() / (1000 * 60 * 60 * 24) } // days
    val ys = recentEntries.map { it.weightKg.toDouble() }

    val n = xs.size.toDouble()
    val sumX = xs.sum()
    val sumY = ys.sum()
    val sumXY = xs.zip(ys).sumOf { it.first * it.second }
    val sumX2 = xs.sumOf { it * it }

    val denominator = n * sumX2 - sumX * sumX
    if (denominator == 0.0) return EtaResult(false, null, "Sin variación en fechas.")

    val m = (n * sumXY - sumX * sumY) / denominator
    val b = (sumY - m * sumX) / n

    if (m.isNaN() || b.isNaN()) return EtaResult(false, null, "Error en el cálculo.")

    if (Math.abs(m) < 0.01) return EtaResult(false, null, "Tendencia casi nula.")

    val lastWeight = ys.last().toFloat()
    val isLosingWeight = targetKg < lastWeight
    if (isLosingWeight && m > 0) return EtaResult(false, null, "La tendencia actual es de subida.")
    if (!isLosingWeight && m < 0) return EtaResult(false, null, "La tendencia actual es de bajada.")


    val xTarget = (targetKg - b) / m
    if (xTarget.isNaN()) return EtaResult(false, null, "No se puede estimar una fecha.")

    val epochMillis = (xTarget * 24 * 60 * 60 * 1000).toLong()
    val estimatedDate =
        Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()

    if (estimatedDate.isBefore(LocalDate.now())) {
        return EtaResult(false, null, "La fecha estimada es en el pasado.")
    }

    return EtaResult(
        true,
        estimatedDate,
        "Fecha estimada: ${estimatedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}"
    )
}
