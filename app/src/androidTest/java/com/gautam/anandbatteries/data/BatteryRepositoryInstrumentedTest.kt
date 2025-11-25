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
class BatteryRepositoryInstrumentedTest {

    private lateinit var database: BatteryDatabase
    private lateinit var repository: BatteryRepository
    private lateinit var batteryDao: BatteryDao
    private lateinit var cartDao: CartDao

    private val sampleBattery1 = Battery(
        id = "1",
        name = "Car Battery A",
        model = "CA-100",
        price = 100.0,
        voltage = "12V",
        capacity = "100Ah",
        warranty = "1 Year",
        description = "Test description",
        imageUrl = "https://example.com/test.jpg",
        category = "Car",
        inStock = true
    )

    private val sampleBattery2 = Battery(
        id = "2",
        name = "Truck Battery B",
        model = "TB-200",
        price = 200.0,
        voltage = "24V",
        capacity = "200Ah",
        warranty = "2 Years",
        description = "Test description 2",
        imageUrl = "https://example.com/test2.jpg",
        category = "Truck",
        inStock = true
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            BatteryDatabase::class.java
        ).allowMainThreadQueries().build()
        batteryDao = database.batteryDao()
        cartDao = database.cartDao()
        repository = BatteryRepository(batteryDao, cartDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Battery Operations Tests

    @Test
    fun getAllBatteries_returnsEmptyList_whenDatabaseIsEmpty() = runTest {
        // When
        val result = repository.getAllBatteries().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun insertBatteries_andGetAllBatteries_returnsBatteries() = runTest {
        // Given
        val batteries = listOf(sampleBattery1, sampleBattery2)

        // When
        repository.insertBatteries(batteries)
        val result = repository.getAllBatteries().first()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.contains(sampleBattery1))
        assertTrue(result.contains(sampleBattery2))
    }

    @Test
    fun getBatteryById_returnsBattery_whenExists() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1))

        // When
        val result = repository.getBatteryById("1")

        // Then
        assertNotNull(result)
        assertEquals(sampleBattery1, result)
    }

    @Test
    fun getBatteryById_returnsNull_whenDoesNotExist() = runTest {
        // When
        val result = repository.getBatteryById("999")

        // Then
        assertNull(result)
    }

    @Test
    fun getBatteriesByCategory_returnsFilteredBatteries() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1, sampleBattery2))

        // When
        val result = repository.getBatteriesByCategory("Car").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Car", result[0].category)
    }

    @Test
    fun searchBatteries_returnsBatteriesMatchingQuery() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1, sampleBattery2))

        // When
        val result = repository.searchBatteries("Truck").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Truck Battery B", result[0].name)
    }

    // Cart Operations Tests

    @Test
    fun addToCart_addsNewItem_whenNotInCart() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1))

        // When
        repository.addToCart("1")
        val cartItems = repository.getAllCartItems().first()

        // Then
        assertEquals(1, cartItems.size)
        assertEquals("1", cartItems[0].batteryId)
        assertEquals(1, cartItems[0].quantity)
    }

    @Test
    fun addToCart_incrementsQuantity_whenItemExists() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1))
        repository.addToCart("1")

        // When
        repository.addToCart("1")
        val cartItems = repository.getAllCartItems().first()

        // Then
        assertEquals(1, cartItems.size)
        assertEquals(2, cartItems[0].quantity)
    }

    @Test
    fun addToCart_multipleItems_worksCorrectly() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1, sampleBattery2))

        // When
        repository.addToCart("1")
        repository.addToCart("2")
        val cartItems = repository.getAllCartItems().first()

        // Then
        assertEquals(2, cartItems.size)
    }

    @Test
    fun updateCartItemQuantity_updatesQuantity_whenPositive() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1))
        repository.addToCart("1")

        // When
        repository.updateCartItemQuantity("1", 5)
        val cartItems = repository.getAllCartItems().first()

        // Then
        assertEquals(1, cartItems.size)
        assertEquals(5, cartItems[0].quantity)
    }

    @Test
    fun updateCartItemQuantity_removesItem_whenZero() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1))
        repository.addToCart("1")

        // When
        repository.updateCartItemQuantity("1", 0)
        val cartItems = repository.getAllCartItems().first()

        // Then
        assertTrue(cartItems.isEmpty())
    }

    @Test
    fun updateCartItemQuantity_removesItem_whenNegative() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1))
        repository.addToCart("1")

        // When
        repository.updateCartItemQuantity("1", -1)
        val cartItems = repository.getAllCartItems().first()

        // Then
        assertTrue(cartItems.isEmpty())
    }

    @Test
    fun removeFromCart_removesItem() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1, sampleBattery2))
        repository.addToCart("1")
        repository.addToCart("2")

        // When
        repository.removeFromCart("1")
        val cartItems = repository.getAllCartItems().first()

        // Then
        assertEquals(1, cartItems.size)
        assertEquals("2", cartItems[0].batteryId)
    }

    @Test
    fun clearCart_removesAllItems() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1, sampleBattery2))
        repository.addToCart("1")
        repository.addToCart("2")

        // When
        repository.clearCart()
        val cartItems = repository.getAllCartItems().first()

        // Then
        assertTrue(cartItems.isEmpty())
    }

    @Test
    fun getCartItemCount_returnsCorrectCount() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1, sampleBattery2))
        repository.addToCart("1")
        repository.addToCart("2")

        // When
        val count = repository.getCartItemCount().first()

        // Then
        assertEquals(2, count)
    }

    @Test
    fun getCartItemsWithBatteries_combinesBatteryAndCartData() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1, sampleBattery2))
        repository.addToCart("1")
        repository.addToCart("1") // quantity = 2
        repository.addToCart("2") // quantity = 1

        // When
        val result = repository.getCartItemsWithBatteries().first()

        // Then
        assertEquals(2, result.size)

        val item1 = result.find { it.battery.id == "1" }
        assertNotNull(item1)
        assertEquals(2, item1?.quantity)
        assertEquals(sampleBattery1, item1?.battery)

        val item2 = result.find { it.battery.id == "2" }
        assertNotNull(item2)
        assertEquals(1, item2?.quantity)
        assertEquals(sampleBattery2, item2?.battery)
    }

    @Test
    fun getCartItemsWithBatteries_filtersOutItemsWithoutBattery() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1))
        repository.addToCart("1")
        // Manually add a cart item without corresponding battery
        cartDao.insertCartItem(CartItem("999", 1))

        // When
        val result = repository.getCartItemsWithBatteries().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].battery.id)
    }

    @Test
    fun complexCartFlow_worksCorrectly() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1, sampleBattery2))

        // Add items to cart
        repository.addToCart("1")
        repository.addToCart("1")
        repository.addToCart("2")

        var cartItems = repository.getCartItemsWithBatteries().first()
        assertEquals(2, cartItems.size)

        // Update quantity
        repository.updateCartItemQuantity("1", 5)
        cartItems = repository.getCartItemsWithBatteries().first()
        assertEquals(5, cartItems.find { it.battery.id == "1" }?.quantity)

        // Remove one item
        repository.removeFromCart("2")
        cartItems = repository.getCartItemsWithBatteries().first()
        assertEquals(1, cartItems.size)

        // Clear cart
        repository.clearCart()
        cartItems = repository.getCartItemsWithBatteries().first()
        assertTrue(cartItems.isEmpty())
    }

    @Test
    fun getCartItemsWithBatteries_returnsEmptyList_whenCartIsEmpty() = runTest {
        // Given
        repository.insertBatteries(listOf(sampleBattery1))

        // When
        val result = repository.getCartItemsWithBatteries().first()

        // Then
        assertTrue(result.isEmpty())
    }
}

