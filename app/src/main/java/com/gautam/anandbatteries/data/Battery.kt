package com.gautam.anandbatteries.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batteries")
data class Battery(
    @PrimaryKey
    val id: String,
    val name: String,
    val model: String,
    val price: Double,
    val voltage: String,
    val capacity: String,
    val warranty: String,
    val description: String,
    val imageUrl: String,
    val category: String,
    val inStock: Boolean = true
)

