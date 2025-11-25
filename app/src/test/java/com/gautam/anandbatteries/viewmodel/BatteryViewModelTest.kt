package com.gautam.anandbatteries.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gautam.anandbatteries.data.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class BatteryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: BatteryViewModel
    private lateinit var repository: BatteryRepository

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

    private val sampleBattery3 = Battery(
        id = "3",
        name = "Car Battery C",
        model = "CC-150",
        price = 150.0,
        voltage = "12V",
        capacity = "150Ah",
        warranty = "1.5 Years",
        description = "Test description 3",
        imageUrl = "https://example.com/test3.jpg",
        category = "Car",
        inStock = true
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = BatteryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state has empty search query`() {
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `initial state has All category selected`() {
        assertEquals("All", viewModel.selectedCategory.value)
    }

    @Test
    fun `updateSearchQuery updates search query state`() {
        // When
        viewModel.updateSearchQuery("test query")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals("test query", viewModel.searchQuery.value)
    }

    @Test
    fun `updateSelectedCategory updates selected category state`() {
        // When
        viewModel.updateSelectedCategory("Car")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals("Car", viewModel.selectedCategory.value)
    }

    @Test
    fun `batteries flow returns all batteries when category is All and search is empty`() = runTest {
        // Given
        val batteries = listOf(sampleBattery1, sampleBattery2, sampleBattery3)
        every { repository.getAllBatteries() } returns flowOf(batteries)

        // When
        val viewModel = BatteryViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(3, viewModel.batteries.value.size)
    }

    @Test
    fun `batteries flow filters by category`() = runTest {
        // Given
        val batteries = listOf(sampleBattery1, sampleBattery2, sampleBattery3)
        every { repository.getAllBatteries() } returns flowOf(batteries)

        // When
        val viewModel = BatteryViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSelectedCategory("Car")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.batteries.value.size)
        assertTrue(viewModel.batteries.value.all { it.category == "Car" })
    }

    @Test
    fun `batteries flow filters by search query`() = runTest {
        // Given
        val batteries = listOf(sampleBattery1, sampleBattery2, sampleBattery3)
        every { repository.getAllBatteries() } returns flowOf(batteries)

        // When
        val viewModel = BatteryViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSearchQuery("Truck")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.batteries.value.size)
        assertEquals("Truck Battery B", viewModel.batteries.value[0].name)
    }

    @Test
    fun `batteries flow filters by both category and search query`() = runTest {
        // Given
        val batteries = listOf(sampleBattery1, sampleBattery2, sampleBattery3)
        every { repository.getAllBatteries() } returns flowOf(batteries)

        // When
        val viewModel = BatteryViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSelectedCategory("Car")
        viewModel.updateSearchQuery("Battery C")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.batteries.value.size)
        assertEquals("Car Battery C", viewModel.batteries.value[0].name)
    }

    @Test
    fun `addToCart calls repository addToCart`() = runTest {
        // Given
        every { repository.getAllBatteries() } returns flowOf(emptyList())
        coEvery { repository.addToCart("1") } just Runs

        // When
        viewModel.addToCart("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.addToCart("1") }
    }

    @Test
    fun `updateCartItemQuantity calls repository updateCartItemQuantity`() = runTest {
        // Given
        every { repository.getAllBatteries() } returns flowOf(emptyList())
        coEvery { repository.updateCartItemQuantity("1", 5) } just Runs

        // When
        viewModel.updateCartItemQuantity("1", 5)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.updateCartItemQuantity("1", 5) }
    }

    @Test
    fun `removeFromCart calls repository removeFromCart`() = runTest {
        // Given
        every { repository.getAllBatteries() } returns flowOf(emptyList())
        coEvery { repository.removeFromCart("1") } just Runs

        // When
        viewModel.removeFromCart("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.removeFromCart("1") }
    }

    @Test
    fun `clearCart calls repository clearCart`() = runTest {
        // Given
        every { repository.getAllBatteries() } returns flowOf(emptyList())
        coEvery { repository.clearCart() } just Runs

        // When
        viewModel.clearCart()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.clearCart() }
    }

    @Test
    fun `cartItemCount reflects repository data`() = runTest {
        // Given
        every { repository.getAllBatteries() } returns flowOf(emptyList())
        every { repository.getCartItemCount() } returns flowOf(5)

        // When
        val viewModel = BatteryViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(5, viewModel.cartItemCount.value)
    }

    @Test
    fun `cartTotal calculates total price correctly`() = runTest {
        // Given
        val cartItems = listOf(
            CartItemWithBattery(sampleBattery1, 2), // 100 * 2 = 200
            CartItemWithBattery(sampleBattery2, 1)  // 200 * 1 = 200
        )
        every { repository.getAllBatteries() } returns flowOf(emptyList())
        every { repository.getCartItemsWithBatteries() } returns flowOf(cartItems)

        // When
        val viewModel = BatteryViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(400.0, viewModel.cartTotal.value, 0.01)
    }

    @Test
    fun `initializeSampleData inserts batteries when database is empty`() = runTest {
        // Given
        every { repository.getAllBatteries() } returns flowOf(emptyList())
        coEvery { repository.insertBatteries(any()) } just Runs

        // When
        val viewModel = BatteryViewModel(repository)
        viewModel.initializeSampleData()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.insertBatteries(any()) }
    }

    @Test
    fun `initializeSampleData does not insert batteries when database has data`() = runTest {
        // Given
        val batteries = listOf(sampleBattery1)
        every { repository.getAllBatteries() } returns flowOf(batteries)

        // When
        val viewModel = BatteryViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.initializeSampleData()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { repository.insertBatteries(any()) }
    }

    @Test
    fun `search is case insensitive for battery name`() = runTest {
        // Given
        val batteries = listOf(sampleBattery1, sampleBattery2)
        every { repository.getAllBatteries() } returns flowOf(batteries)

        // When
        val viewModel = BatteryViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSearchQuery("TRUCK")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.batteries.value.size)
        assertEquals("Truck Battery B", viewModel.batteries.value[0].name)
    }

    @Test
    fun `search works for model number`() = runTest {
        // Given
        val batteries = listOf(sampleBattery1, sampleBattery2)
        every { repository.getAllBatteries() } returns flowOf(batteries)

        // When
        val viewModel = BatteryViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSearchQuery("TB-200")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.batteries.value.size)
        assertEquals(sampleBattery2, viewModel.batteries.value[0])
    }
}

