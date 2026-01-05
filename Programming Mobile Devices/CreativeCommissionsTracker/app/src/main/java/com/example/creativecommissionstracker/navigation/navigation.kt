package com.example.creativecommissionstracker.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object Orders : Screen("orders")

    data object OrderDetails : Screen("order_details/{orderId}") {
        fun createRoute(orderId: String) = "order_details/$orderId"
    }

    data object AddOrder : Screen("add_order")

    data object Clients : Screen("clients")
}