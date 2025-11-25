package com.gautam.anandbatteries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gautam.anandbatteries.data.BatteryDatabase
import com.gautam.anandbatteries.data.BatteryRepository
import com.gautam.anandbatteries.ui.screens.BatteryListScreen
import com.gautam.anandbatteries.ui.screens.CartScreen
import com.gautam.anandbatteries.ui.screens.CheckoutScreen
import com.gautam.anandbatteries.ui.theme.AnandBatteriesTheme
import com.gautam.anandbatteries.viewmodel.BatteryViewModel
import com.gautam.anandbatteries.viewmodel.BatteryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnandBatteriesTheme {
                val context = LocalContext.current
                val database = BatteryDatabase.getDatabase(context)
                val repository = BatteryRepository(database.batteryDao(), database.cartDao())
                val viewModelFactory = BatteryViewModelFactory(repository)
                val batteryViewModel: BatteryViewModel = viewModel(factory = viewModelFactory)

                // Initialize sample data on first launch
                LaunchedEffect(Unit) {
                    batteryViewModel.initializeSampleData()
                }

                AnandBatteriesApp(viewModel = batteryViewModel)
            }
        }
    }
}

@Composable
fun AnandBatteriesApp(viewModel: BatteryViewModel) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> {
                    BatteryListScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.CART -> {
                    CartScreen(
                        viewModel = viewModel,
                        onCheckout = { currentDestination = AppDestinations.CHECKOUT },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.CHECKOUT -> {
                    CheckoutScreen(
                        viewModel = viewModel,
                        onOrderPlaced = { currentDestination = AppDestinations.HOME },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    CART("Cart", Icons.Default.ShoppingCart),
    CHECKOUT("Checkout", Icons.Default.CheckCircle),
}