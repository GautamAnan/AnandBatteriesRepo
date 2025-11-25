package com.gautam.anandbatteries.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gautam.anandbatteries.data.Battery
import com.gautam.anandbatteries.data.BatteryRepository
import com.gautam.anandbatteries.data.CartItemWithBattery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BatteryViewModel(
    private val repository: BatteryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    // All batteries from database
    private val allBatteries = repository.getAllBatteries()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filtered batteries based on search and category
    val batteries: StateFlow<List<Battery>> = combine(
        allBatteries,
        _searchQuery,
        _selectedCategory
    ) { batteries, query, category ->
        batteries
            .filter { battery ->
                (category == "All" || battery.category == category) &&
                (query.isBlank() || battery.name.contains(query, ignoreCase = true) ||
                 battery.model.contains(query, ignoreCase = true))
            }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val cartItems: StateFlow<List<CartItemWithBattery>> = repository.getCartItemsWithBatteries()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val cartItemCount: StateFlow<Int> = repository.getCartItemCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val cartTotal: StateFlow<Double> = cartItems
        .map { items -> items.sumOf { it.battery.price * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addToCart(batteryId: String) {
        viewModelScope.launch {
            repository.addToCart(batteryId)
        }
    }

    fun updateCartItemQuantity(batteryId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartItemQuantity(batteryId, quantity)
        }
    }

    fun removeFromCart(batteryId: String) {
        viewModelScope.launch {
            repository.removeFromCart(batteryId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun initializeSampleData() {
        viewModelScope.launch {
            // Check if database is empty
            if (allBatteries.value.isEmpty()) {
                repository.insertBatteries(com.gautam.anandbatteries.data.SampleData.getSampleBatteries())
            }
        }
    }
}

class BatteryViewModelFactory(
    private val repository: BatteryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BatteryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BatteryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

