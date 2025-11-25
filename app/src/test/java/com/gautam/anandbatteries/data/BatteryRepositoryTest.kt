package com.gautam.anandbatteries.data

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class BatteryRepositoryTest {

    private lateinit var repository: BatteryRepository
    private lateinit var batteryDao: BatteryDao
    private lateinit var cartDao: CartDao

    private val sampleBattery = Battery(
        id = "1",
        name = "Test Battery",
        model = "TB-100",
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
        name = "Test Battery 2",
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
        batteryDao = mockk()
        cartDao = mockk()
        repository = BatteryRepository(batteryDao, cartDao)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    // Battery Operations Tests

    @Test
    fun `getAllBatteries returns flow of batteries from DAO`() = runTest {
        // Given
        val batteries = listOf(sampleBattery, sampleBattery2)
        every { batteryDao.getAllBatteries() } returns flowOf(batteries)

        // When
        val result = repository.getAllBatteries().first()

        // Then
        assertEquals(2, result.size)
        assertEquals(sampleBattery, result[0])
        assertEquals(sampleBattery2, result[1])
        verify { batteryDao.getAllBatteries() }
    }

    @Test
    fun `getBatteryById returns battery when found`() = runTest {
        // Given
        coEvery { batteryDao.getBatteryById("1") } returns sampleBattery

        // When
        val result = repository.getBatteryById("1")

        // Then
        assertNotNull(result)
        assertEquals(sampleBattery, result)
        coVerify { batteryDao.getBatteryById("1") }
    }

    @Test
    fun `getBatteryById returns null when not found`() = runTest {
        // Given
        coEvery { batteryDao.getBatteryById("999") } returns null

        // When
        val result = repository.getBatteryById("999")

        // Then
        assertNull(result)
        coVerify { batteryDao.getBatteryById("999") }
    }

    @Test
    fun `getBatteriesByCategory returns filtered batteries`() = runTest {
        // Given
        val carBatteries = listOf(sampleBattery)
        every { batteryDao.getBatteriesByCategory("Car") } returns flowOf(carBatteries)

        // When
        val result = repository.getBatteriesByCategory("Car").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Car", result[0].category)
        verify { batteryDao.getBatteriesByCategory("Car") }
    }

    @Test
    fun `searchBatteries returns matching batteries`() = runTest {
        // Given
        val query = "Test"
        val batteries = listOf(sampleBattery, sampleBattery2)
        every { batteryDao.searchBatteries(query) } returns flowOf(batteries)

        // When
        val result = repository.searchBatteries(query).first()

        // Then
        assertEquals(2, result.size)
        verify { batteryDao.searchBatteries(query) }
    }

    @Test
    fun `insertBatteries inserts batteries through DAO`() = runTest {
        // Given
        val batteries = listOf(sampleBattery, sampleBattery2)
        coEvery { batteryDao.insertBatteries(batteries) } just Runs

        // When
        repository.insertBatteries(batteries)

        // Then
        coVerify { batteryDao.insertBatteries(batteries) }
    }

    // Cart Operations Tests

    @Test
    fun `getAllCartItems returns flow of cart items from DAO`() = runTest {
        // Given
        val cartItems = listOf(CartItem("1", 2), CartItem("2", 1))
        every { cartDao.getAllCartItems() } returns flowOf(cartItems)

        // When
        val result = repository.getAllCartItems().first()

        // Then
        assertEquals(2, result.size)
        verify { cartDao.getAllCartItems() }
    }

    @Test
    fun `addToCart adds new item when not in cart`() = runTest {
        // Given
        coEvery { cartDao.getCartItem("1") } returns null
        coEvery { cartDao.insertCartItem(any()) } just Runs

        // When
        repository.addToCart("1")

        // Then
        coVerify { cartDao.getCartItem("1") }
        coVerify { cartDao.insertCartItem(CartItem("1", 1)) }
    }

    @Test
    fun `addToCart increments quantity when item exists`() = runTest {
        // Given
        val existingItem = CartItem("1", 2)
        coEvery { cartDao.getCartItem("1") } returns existingItem
        coEvery { cartDao.updateQuantity("1", 3) } just Runs

        // When
        repository.addToCart("1")

        // Then
        coVerify { cartDao.getCartItem("1") }
        coVerify { cartDao.updateQuantity("1", 3) }
    }

    @Test
    fun `updateCartItemQuantity updates quantity when positive`() = runTest {
        // Given
        coEvery { cartDao.updateQuantity("1", 5) } just Runs

        // When
        repository.updateCartItemQuantity("1", 5)

        // Then
        coVerify { cartDao.updateQuantity("1", 5) }
    }

    @Test
    fun `updateCartItemQuantity removes item when quantity is zero`() = runTest {
        // Given
        coEvery { cartDao.removeCartItem("1") } just Runs

        // When
        repository.updateCartItemQuantity("1", 0)

        // Then
        coVerify { cartDao.removeCartItem("1") }
    }

    @Test
    fun `updateCartItemQuantity removes item when quantity is negative`() = runTest {
        // Given
        coEvery { cartDao.removeCartItem("1") } just Runs

        // When
        repository.updateCartItemQuantity("1", -1)

        // Then
        coVerify { cartDao.removeCartItem("1") }
    }

    @Test
    fun `removeFromCart removes item from cart`() = runTest {
        // Given
        coEvery { cartDao.removeCartItem("1") } just Runs

        // When
        repository.removeFromCart("1")

        // Then
        coVerify { cartDao.removeCartItem("1") }
    }

    @Test
    fun `clearCart clears all items`() = runTest {
        // Given
        coEvery { cartDao.clearCart() } just Runs

        // When
        repository.clearCart()

        // Then
        coVerify { cartDao.clearCart() }
    }

    @Test
    fun `getCartItemCount returns count from DAO`() = runTest {
        // Given
        every { cartDao.getCartItemCount() } returns flowOf(5)

        // When
        val result = repository.getCartItemCount().first()

        // Then
        assertEquals(5, result)
        verify { cartDao.getCartItemCount() }
    }

    @Test
    fun `getCartItemsWithBatteries combines cart items with battery data`() = runTest {
        // Given
        val cartItems = listOf(CartItem("1", 2), CartItem("2", 1))
        val batteries = listOf(sampleBattery, sampleBattery2)
        every { cartDao.getAllCartItems() } returns flowOf(cartItems)
        every { batteryDao.getAllBatteries() } returns flowOf(batteries)

        // When
        val result = repository.getCartItemsWithBatteries().first()

        // Then
        assertEquals(2, result.size)
        assertEquals(sampleBattery, result[0].battery)
        assertEquals(2, result[0].quantity)
        assertEquals(sampleBattery2, result[1].battery)
        assertEquals(1, result[1].quantity)
    }

    @Test
    fun `getCartItemsWithBatteries filters out items with no matching battery`() = runTest {
        // Given
        val cartItems = listOf(CartItem("1", 2), CartItem("999", 1))
        val batteries = listOf(sampleBattery)
        every { cartDao.getAllCartItems() } returns flowOf(cartItems)
        every { batteryDao.getAllBatteries() } returns flowOf(batteries)

        // When
        val result = repository.getCartItemsWithBatteries().first()

        // Then
        assertEquals(1, result.size)
        assertEquals(sampleBattery, result[0].battery)
    }
}

