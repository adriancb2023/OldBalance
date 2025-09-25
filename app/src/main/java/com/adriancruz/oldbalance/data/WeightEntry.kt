package com.adriancruz.oldbalance.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa un registro de peso en la base de datos.
 *
 * @property id el identificador unico del registro.
 * @property date la fecha del registro en milisegundos.
 * @property weightKg el peso en kilogramos.
 * @property note una nota opcional sobre el registro.
 * @property createdAt la fecha de creacion del registro en milisegundos.
 */
@Entity(tableName = "weight_entries",
        indices = [Index(value = ["date"], unique = true)])
data class WeightEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val weightKg: Float,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
