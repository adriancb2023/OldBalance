package com.adriancruz.oldbalance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un objetivo de peso en la base de datos.
 *
 * @property id el identificador unico del objetivo.
 * @property targetKg el peso objetivo en kilogramos.
 * @property startDate la fecha de inicio del objetivo en milisegundos.
 * @property initialWeight el peso inicial al comenzar el objetivo.
 * @property endDate la fecha de finalizacion del objetivo.
 * @property colorHex el color asociado al objetivo en formato hexadecimal.
 * @property isActive indica si el objetivo esta activo.
 * @property createdAt la fecha de creacion del objetivo en milisegundos.
 */
@Entity(tableName = "weight_goals")
data class WeightGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetKg: Float,
    val startDate: Long = System.currentTimeMillis(),
    val initialWeight: Float? = null,
    val endDate: Long? = null,
    val colorHex: String = "#F59E0B",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
