package com.example.fitnesstrackerapp.userInterface

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitnesstrackerapp.nav.Dest
import com.example.fitnesstrackerapp.nav.bottomItems
import com.example.fitnesstrackerapp.userInterface.ActivitiesScreen
import com.example.fitnesstrackerapp.userInterface.DetailsScreen
import com.example.fitnesstrackerapp.userInterface.LogsScreen
import com.example.fitnesstrackerapp.viewModel.FitnessViewModel

// hosts Bottom Navigation
// meets requirement: implement Bottom Navigation with at least two tabs
// shares one ViewModel across screens
// shows a Toast when saving a log
@Composable
fun FitnessApp() {
    val vm: FitnessViewModel = viewModel()   // shared ViewModel
    val navController = rememberNavController()
    val context = LocalContext.current

    // one time UI message (example: "saved")
    vm.uiMessage?.let { msg ->
        SideEffect {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            vm.consumeMessage()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val current by navController.currentBackStackEntryAsState()
                val currentRoute = current?.destination?.route
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = when (item.dest) {
                            Dest.Items -> currentRoute == Dest.Items.route
                            Dest.Logs -> currentRoute == Dest.Logs.route
                            else -> false
                        },
                        onClick = {
                            navController.navigate(item.dest.route) {
                                launchSingleTop = true  // prevents multiple copies
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.label)},
                        label = {
                            Text(item.label)}
                    )
                }
            }
        }
    ) { padding ->
        // 3 destinations: items(activities), Details, Logs
        NavHost(
            navController = navController,
            startDestination = Dest.Items.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Dest.Items.route) {
                // Screen 1: Item List of Activities
                ActivitiesScreen(
                    vm = vm,
                    onOpenDetails = { id -> navController.navigate(Dest.Details.routeFor(id)) }
                )
            }
            composable(Dest.Logs.route) {
                // Screen 3: Logs
                LogsScreen(vm = vm)
            }
            composable(Dest.Details.route) { backStack ->
                // Screen 2: Details
                val id = backStack.arguments?.getString("id")
                DetailsScreen(
                    vm = vm,
                    activityId = id,
                    onBack = {          // back button
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
