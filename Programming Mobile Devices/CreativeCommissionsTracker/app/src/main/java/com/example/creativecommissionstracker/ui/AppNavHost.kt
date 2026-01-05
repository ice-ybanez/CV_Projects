package com.example.creativecommissionstracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.creativecommissionstracker.AppViewModel
import com.example.creativecommissionstracker.navigation.Screen
import com.example.creativecommissionstracker.ui.clients.ClientsScreen
import com.example.creativecommissionstracker.ui.home.HomeScreen
import com.example.creativecommissionstracker.ui.orders.AddOrderScreen
import com.example.creativecommissionstracker.ui.orders.OrdersScreen
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.List
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.creativecommissionstracker.ui.orders.OrderDetailsScreen


@Composable
fun AppNavHost(
    appViewModel: AppViewModel,
    navController: NavHostController = rememberNavController()
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            BottomBar(
                currentDestination = currentDestination,
                navController = navController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Screen.Home.route) {
                HomeScreen(
                    appViewModel = appViewModel,
                    navController = navController
                )
            }

            composable(Screen.Clients.route) {
                ClientsScreen()
            }

            composable(Screen.Orders.route) {
                OrdersScreen(navController = navController)
            }

            composable(Screen.AddOrder.route) {
                AddOrderScreen(navController = navController)
            }

            composable(
                route = Screen.OrderDetails.route,
                arguments = listOf(
                    navArgument("orderId") { type = NavType.StringType }
                )
            ){ backstackEntry ->
                val orderId = backstackEntry.arguments?.getString("orderId") ?: ""

                OrderDetailsScreen(
                    navController = navController,
                    orderId = orderId
                )


            }
        }
    }
}

@Composable
private fun BottomBar(
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    // only top-level screens in bottom nav, no nested screens like AddOrder or OrderDetails
    val items = listOf(
        Screen.Home,
        Screen.Clients,
        Screen.Orders
    )

    NavigationBar {
        items.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            val (label, icon) = when (screen) {
                Screen.Home -> "Home" to Icons.Filled.Home
                Screen.Clients -> "Clients" to Icons.Filled.Person
                Screen.Orders -> "Orders" to Icons.Filled.List
                else -> screen.route to Icons.Filled.Home
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        // avoid building up a huge back stack when switching tabs
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}


