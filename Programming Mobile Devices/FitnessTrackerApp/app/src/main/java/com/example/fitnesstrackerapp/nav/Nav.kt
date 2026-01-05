package com.example.fitnesstrackerapp.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensitySmall
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

// navigation destinations
sealed class Dest(val route: String) {
    data object Items : Dest("items")
    data object Logs : Dest("logs")
    data object Details : Dest("details/{id}") {
        fun routeFor(id: String) = "details/$id"
    }
}

// bottom navigation items
data class BottomItem(
    val dest: Dest,
    val label: String,
    val icon: ImageVector
)

val bottomItems = listOf(
    BottomItem(
        Dest.Items,
        "Items",
        Icons.Filled.DensitySmall), // icon name doesn't match label but it looks good
    BottomItem(
        Dest.Logs,
        "Logs",
        Icons.Filled.Info)
)
