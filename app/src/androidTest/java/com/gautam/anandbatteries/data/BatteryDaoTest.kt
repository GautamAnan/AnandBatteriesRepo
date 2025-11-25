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
class BatteryDaoTest {

    private lateinit var database: BatteryDatabase
    private lateinit var batteryDao: BatteryDao

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
        name = "Bike Battery C",
        model = "BB-50",
        price = 50.0,
        voltage = "6V",
        capacity = "50Ah",
        warranty = "6 Months",
        description = "Test description 3",
        imageUrl = "https://example.com/test3.jpg",
        category = "Bike",
        inStock = false
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            BatteryDatabase::class.java
        ).allowMainThreadQueries().build()
        batteryDao = database.batteryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertBatteries_andGetAllBatteries_returnsAllBatteries() = runTest {
        // Given
        val batteries = listOf(sampleBattery1, sampleBattery2, sampleBattery3)

        // When
        batteryDao.insertBatteries(batteries)
        val result = batteryDao.getAllBatteries().first()

        // Then
        assertEquals(3, result.size)
        assertTrue(result.contains(sampleBattery1))
        assertTrue(result.contains(sampleBattery2))
        assertTrue(result.contains(sampleBattery3))
    }

    @Test
    fun getAllBatteries_returnsEmptyList_whenDatabaseIsEmpty() = runTest {
        // When
        val result = batteryDao.getAllBatteries().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun getAllBatteries_returnsBatteriesSortedByName() = runTest {
        // Given
        val batteries = listOf(sampleBattery3, sampleBattery1, sampleBattery2)
        batteryDao.insertBatteries(batteries)

        // When
        val result = batteryDao.getAllBatteries().first()

        // Then
        assertEquals("Bike Battery C", result[0].name)
        assertEquals("Car Battery A", result[1].name)
        assertEquals("Truck Battery B", result[2].name)
    }

    @Test
    fun getBatteryById_returnsBattery_whenExists() = runTest {
        // Given
        batteryDao.insertBatteries(listOf(sampleBattery1))

        // When
        val result = batteryDao.getBatteryById("1")

        // Then
        assertNotNull(result)
        assertEquals(sampleBattery1, result)
    }

    @Test
    fun getBatteryById_returnsNull_whenDoesNotExist() = runTest {
        // When
        val result = batteryDao.getBatteryById("999")

        // Then
        assertNull(result)
    }

    @Test
    fun getBatteriesByCategory_returnsOnlyMatchingCategory() = runTest {
        // Given
        val batteries = listOf(sampleBattery1, sampleBattery2, sampleBattery3)
        batteryDao.insertBatteries(batteries)

        // When
        val result = batteryDao.getBatteriesByCategory("Car").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Car", result[0].category)
        assertEquals(sampleBattery1, result[0])
    }

    @Test
    fun getBatteriesByCategory_returnsEmptyList_whenNoBatteriesInCategory() = runTest {
        // Given
        batteryDao.insertBatteries(listOf(sampleBattery1))

        // When
        val result = batteryDao.getBatteriesByCategory("NonExistentCategory").first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun searchBatteries_findsBatteriesByName() = runTest {
        // Given
        batteryDao.insertBatteries(listOf(sampleBattery1, sampleBattery2, sampleBattery3))

        // When
        val result = batteryDao.searchBatteries("Car").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Car Battery A", result[0].name)
    }

    @Test
    fun searchBatteries_findsBatteriesByModel() = runTest {
        // Given
        batteryDao.insertBatteries(listOf(sampleBattery1, sampleBattery2, sampleBattery3))

        // When
        val result = batteryDao.searchBatteries("TB-200").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Truck Battery B", result[0].name)
    }

    @Test
    fun searchBatteries_isCaseInsensitive() = runTest {
        // Given
        batteryDao.insertBatteries(listOf(sampleBattery1, sampleBattery2))

        // When
        val result = batteryDao.searchBatteries("truck").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Truck Battery B", result[0].name)
    }

    @Test
    fun searchBatteries_returnsEmptyList_whenNoMatch() = runTest {
        // Given
        batteryDao.insertBatteries(listOf(sampleBattery1, sampleBattery2))

        // When
        val result = batteryDao.searchBatteries("NonExistent").first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun searchBatteries_findsPartialMatches() = runTest {
        // Given
        batteryDao.insertBatteries(listOf(sampleBattery1, sampleBattery2, sampleBattery3))

        // When
        val result = batteryDao.searchBatteries("Battery").first()

        // Then
        assertEquals(3, result.size)
    }

    @Test
    fun insertBatteries_replacesExistingBattery_onConflict() = runTest {
        // Given
        batteryDao.insertBatteries(listOf(sampleBattery1))
        val updatedBattery = sampleBattery1.copy(price = 150.0)

        // When
        batteryDao.insertBatteries(listOf(updatedBattery))
        val result = batteryDao.getBatteryById("1")

        // Then
        assertNotNull(result)
        assertEquals(150.0, result!!.price, 0.01)
    }

    @Test
    fun deleteAllBatteries_removesAllBatteries() = runTest {
        // Given
        batteryDao.insertBatteries(listOf(sampleBattery1, sampleBattery2, sampleBattery3))

        // When
        batteryDao.deleteAllBatteries()
        val result = batteryDao.getAllBatteries().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun deleteAllBatteries_worksOnEmptyDatabase() = runTest {
        // When
        batteryDao.deleteAllBatteries()
        val result = batteryDao.getAllBatteries().first()

        // Then
        assertTrue(result.isEmpty())
    }
}

