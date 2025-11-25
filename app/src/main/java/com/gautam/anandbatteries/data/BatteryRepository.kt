package com.gautam.anandbatteries.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class BatteryRepository(
    private val batteryDao: BatteryDao,
    private val cartDao: CartDao
) {
    // Battery operations
    fun getAllBatteries(): Flow<List<Battery>> = batteryDao.getAllBatteries()

    suspend fun getBatteryById(batteryId: String): Battery? = batteryDao.getBatteryById(batteryId)

    fun getBatteriesByCategory(category: String): Flow<List<Battery>> =
        batteryDao.getBatteriesByCategory(category)

    fun searchBatteries(query: String): Flow<List<Battery>> = batteryDao.searchBatteries(query)

    suspend fun insertBatteries(batteries: List<Battery>) = batteryDao.insertBatteries(batteries)

    // Cart operations
    fun getAllCartItems(): Flow<List<CartItem>> = cartDao.getAllCartItems()

    fun getCartItemsWithBatteries(): Flow<List<CartItemWithBattery>> {
        return combine(
            cartDao.getAllCartItems(),
            batteryDao.getAllBatteries()
        ) { cartItems, batteries ->
            cartItems.mapNotNull { cartItem ->
                batteries.find { it.id == cartItem.batteryId }?.let { battery ->
                    CartItemWithBattery(battery, cartItem.quantity)
                }
            }
        }
    }

    suspend fun addToCart(batteryId: String) {
        val existingItem = cartDao.getCartItem(batteryId)
        if (existingItem != null) {
            cartDao.updateQuantity(batteryId, existingItem.quantity + 1)
        } else {
            cartDao.insertCartItem(CartItem(batteryId, 1))
        }
    }

    suspend fun updateCartItemQuantity(batteryId: String, quantity: Int) {
        if (quantity <= 0) {
            cartDao.removeCartItem(batteryId)
        } else {
            cartDao.updateQuantity(batteryId, quantity)
        }
    }

    suspend fun removeFromCart(batteryId: String) = cartDao.removeCartItem(batteryId)

    suspend fun clearCart() = cartDao.clearCart()

    fun getCartItemCount(): Flow<Int> = cartDao.getCartItemCount()
}

