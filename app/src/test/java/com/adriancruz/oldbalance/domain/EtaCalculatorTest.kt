package com.adriancruz.oldbalance.domain

import com.adriancruz.oldbalance.data.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class EtaCalculatorTest {

    @Test
    fun `estimateEta returns insufficient data for less than 3 entries`() {
        val entries = listOf(
            WeightEntry(date = 1000L, weightKg = 80f),
            WeightEntry(date = 2000L, weightKg = 79.8f)
        )
        val result = estimateEta(entries, 75f)
        assertFalse(result.reachable)
        assertEquals("Datos insuficientes (mín. 3 registros).", result.message)
    }

    @Test
    fun `estimateEta calculates correct date for clear downward trend`() {

        val today = LocalDate.now()
        val entries = listOf(
            WeightEntry(date = today.minusDays(2).toEpochMilli(), weightKg = 80.2f),
            WeightEntry(date = today.minusDays(1).toEpochMilli(), weightKg = 80.1f),
            WeightEntry(date = today.toEpochMilli(), weightKg = 80.0f)
        )
        val targetKg = 79.0f
        val result = estimateEta(entries, targetKg)

        assertTrue(result.reachable)
        assertNotNull(result.estimatedDate)

        val expectedDate = today.plusDays(10)
        assertEquals(expectedDate, result.estimatedDate)
    }

    @Test
    fun `estimateEta returns unreachable for opposing trend`() {

        val today = LocalDate.now()
        val entries = listOf(
            WeightEntry(date = today.minusDays(2).toEpochMilli(), weightKg = 80.0f),
            WeightEntry(date = today.minusDays(1).toEpochMilli(), weightKg = 80.1f),
            WeightEntry(date = today.toEpochMilli(), weightKg = 80.2f)
        )
        val targetKg = 79.0f
        val result = estimateEta(entries, targetKg)

        assertFalse(result.reachable)
        assertEquals("La tendencia actual es de subida.", result.message)
    }

    @Test
    fun `estimateEta returns null for flat trend`() {
        val today = LocalDate.now()
        val entries = listOf(
            WeightEntry(date = today.minusDays(2).toEpochMilli(), weightKg = 80.0f),
            WeightEntry(date = today.minusDays(1).toEpochMilli(), weightKg = 80.0f),
            WeightEntry(date = today.toEpochMilli(), weightKg = 80.0f)
        )
        val targetKg = 79.0f
        val result = estimateEta(entries, targetKg)

        assertFalse(result.reachable)
        assertEquals("Tendencia casi nula.", result.message)
    }

    private fun LocalDate.toEpochMilli(): Long {
        return this.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
