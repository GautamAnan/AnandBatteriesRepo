package com.gautam.anandbatteries.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey
    val batteryId: String,
    val quantity: Int = 1
)

