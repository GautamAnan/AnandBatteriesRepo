package com.gautam.anandbatteries.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CartDaoTest {

    private lateinit var database: BatteryDatabase
    private lateinit var cartDao: CartDao

    private val cartItem1 = CartItem(batteryId = "1", quantity = 2)
    private val cartItem2 = CartItem(batteryId = "2", quantity = 1)
    private val cartItem3 = CartItem(batteryId = "3", quantity = 5)

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            BatteryDatabase::class.java
        ).allowMainThreadQueries().build()
        cartDao = database.cartDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCartItem_andGetAllCartItems_returnsAllItems() = runTest {
        // Given
        cartDao.insertCartItem(cartItem1)
        cartDao.insertCartItem(cartItem2)

        // When
        val result = cartDao.getAllCartItems().first()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.contains(cartItem1))
        assertTrue(result.contains(cartItem2))
    }

    @Test
    fun getAllCartItems_returnsEmptyList_whenCartIsEmpty() = runTest {
        // When
        val result = cartDao.getAllCartItems().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun getCartItem_returnsItem_whenExists() = runTest {
        // Given
        cartDao.insertCartItem(cartItem1)

        // When
        val result = cartDao.getCartItem("1")

        // Then
        assertNotNull(result)
        assertEquals(cartItem1, result)
    }

    @Test
    fun getCartItem_returnsNull_whenDoesNotExist() = runTest {
        // When
        val result = cartDao.getCartItem("999")

        // Then
        assertNull(result)
    }

    @Test
    fun insertCartItem_replacesExistingItem_onConflict() = runTest {
        // Given
        cartDao.insertCartItem(cartItem1)
        val updatedItem = CartItem(batteryId = "1", quantity = 10)

        // When
        cartDao.insertCartItem(updatedItem)
        val result = cartDao.getCartItem("1")

        // Then
        assertNotNull(result)
        assertEquals(10, result?.quantity)
    }

    @Test
    fun updateQuantity_updatesItemQuantity() = runTest {
        // Given
        cartDao.insertCartItem(cartItem1)

        // When
        cartDao.updateQuantity("1", 7)
        val result = cartDao.getCartItem("1")

        // Then
        assertNotNull(result)
        assertEquals(7, result?.quantity)
    }

    @Test
    fun updateQuantity_doesNothing_whenItemDoesNotExist() = runTest {
        // When
        cartDao.updateQuantity("999", 5)
        val result = cartDao.getCartItem("999")

        // Then
        assertNull(result)
    }

    @Test
    fun removeCartItem_removesItem() = runTest {
        // Given
        cartDao.insertCartItem(cartItem1)
        cartDao.insertCartItem(cartItem2)

        // When
        cartDao.removeCartItem("1")
        val result = cartDao.getAllCartItems().first()

        // Then
        assertEquals(1, result.size)
        assertFalse(result.any { it.batteryId == "1" })
        assertTrue(result.any { it.batteryId == "2" })
    }

    @Test
    fun removeCartItem_doesNothing_whenItemDoesNotExist() = runTest {
        // Given
        cartDao.insertCartItem(cartItem1)

        // When
        cartDao.removeCartItem("999")
        val result = cartDao.getAllCartItems().first()

        // Then
        assertEquals(1, result.size)
    }

    @Test
    fun clearCart_removesAllItems() = runTest {
        // Given
        cartDao.insertCartItem(cartItem1)
        cartDao.insertCartItem(cartItem2)
        cartDao.insertCartItem(cartItem3)

        // When
        cartDao.clearCart()
        val result = cartDao.getAllCartItems().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun clearCart_worksOnEmptyCart() = runTest {
        // When
        cartDao.clearCart()
        val result = cartDao.getAllCartItems().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun getCartItemCount_returnsCorrectCount() = runTest {
        // Given
        cartDao.insertCartItem(cartItem1)
        cartDao.insertCartItem(cartItem2)
        cartDao.insertCartItem(cartItem3)

        // When
        val result = cartDao.getCartItemCount().first()

        // Then
        assertEquals(3, result)
    }

    @Test
    fun getCartItemCount_returnsZero_whenCartIsEmpty() = runTest {
        // When
        val result = cartDao.getCartItemCount().first()

        // Then
        assertEquals(0, result)
    }

    @Test
    fun getCartItemCount_updatesWhenItemAdded() = runTest {
        // Given
        cartDao.insertCartItem(cartItem1)
        val initialCount = cartDao.getCartItemCount().first()

        // When
        cartDao.insertCartItem(cartItem2)
        val finalCount = cartDao.getCartItemCount().first()

        // Then
        assertEquals(1, initialCount)
        assertEquals(2, finalCount)
    }

    @Test
    fun getCartItemCount_updatesWhenItemRemoved() = runTest {
        // Given
        cartDao.insertCartItem(cartItem1)
        cartDao.insertCartItem(cartItem2)
        val initialCount = cartDao.getCartItemCount().first()

        // When
        cartDao.removeCartItem("1")
        val finalCount = cartDao.getCartItemCount().first()

        // Then
        assertEquals(2, initialCount)
        assertEquals(1, finalCount)
    }

    @Test
    fun multipleOperations_workCorrectly() = runTest {
        // Insert items
        cartDao.insertCartItem(cartItem1)
        cartDao.insertCartItem(cartItem2)
        assertEquals(2, cartDao.getCartItemCount().first())

        // Update quantity
        cartDao.updateQuantity("1", 10)
        val item1 = cartDao.getCartItem("1")
        assertEquals(10, item1?.quantity)

        // Remove one item
        cartDao.removeCartItem("2")
        assertEquals(1, cartDao.getCartItemCount().first())

        // Add new item
        cartDao.insertCartItem(cartItem3)
        assertEquals(2, cartDao.getCartItemCount().first())

        // Clear all
        cartDao.clearCart()
        assertEquals(0, cartDao.getCartItemCount().first())
    }
}

