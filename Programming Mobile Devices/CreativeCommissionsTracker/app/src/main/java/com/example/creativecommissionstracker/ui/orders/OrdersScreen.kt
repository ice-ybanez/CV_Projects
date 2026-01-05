package com.example.creativecommissionstracker.ui.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.creativecommissionstracker.model.Order
import com.example.creativecommissionstracker.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Switch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.creativecommissionstracker.notifications.NotificationHelper
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navController: NavHostController,
    vm: OrdersViewModel = viewModel()
) {
    val state = vm.uiState

    // when navigating back to Orders, refetch latest data from Firestore
    LaunchedEffect(Unit) {
        vm.refresh()
    }

    val context = LocalContext.current

    var notificationsEnabled by remember {
        mutableStateOf(NotificationHelper.isNotificationsEnabled(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            notificationsEnabled = true
            NotificationHelper.setNotificationsEnabled(context, true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders") },
                actions = {
                    // Notifications toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notify", style = MaterialTheme.typography.bodySmall)

                        Spacer(Modifier.width(4.dp))

                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { checked ->

                                if (checked) {
                                    // Turning ON
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        val hasPermission = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED

                                        if (!hasPermission) {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            notificationsEnabled = true
                                            NotificationHelper.setNotificationsEnabled(context, true)
                                        }
                                    }
                                    else {

                                        notificationsEnabled = true
                                        NotificationHelper.setNotificationsEnabled(context, true)
                                    }
                                } else {
                                    // Turning OFF
                                    notificationsEnabled = false
                                    NotificationHelper.setNotificationsEnabled(context, false)
                                }
                            }
                        )
                    }

                    TextButton(onClick = { navController.navigate(Screen.AddOrder.route) }) {
                        Text("Add")
                    }
                }
            )

        }
    ) { padding ->

        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            visible = true
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                initialOffsetY = { it / 10 }
            ),
            exit = fadeOut(animationSpec = tween(200)) + slideOutVertically(
                targetOffsetY = { it / 10 }
            )
        ) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    if (state.orders.isEmpty()) {
                        Text(
                            text = "No orders found.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.orders) { order ->
                                val clientName = state.clients
                                    .find { it.id == order.clientId }
                                    ?.name ?: "Unknown client"

                                OrderCard(
                                    order = order,
                                    clientName = clientName,
                                    onMarkDone = { vm.markOrderDone(order.id) },
                                    onMarkPaid = { vm.markOrderPaid(order.id) },
                                    onOpenDetails = {

                                        navController.navigate(
                                            Screen.OrderDetails.createRoute(order.id)
                                        )

                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun OrderCard(
    order: Order,
    clientName: String,
    onMarkDone: () -> Unit,
    onMarkPaid: () -> Unit,
    onOpenDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = order.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Client: $clientName",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Art: ${order.artType} • ${order.medium} • ${order.style}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Price: €${order.price.toInt()}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Due: ${formatDueDate(order.dueDate)}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Status: ${order.status.replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = if (order.isPaid) "Paid" else "Not paid",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onMarkDone,
                    enabled = order.status != "done"
                ) {
                    Text("Mark Done")
                }
                TextButton(
                    onClick = onMarkPaid,
                    enabled = !order.isPaid
                ) {
                    Text("Mark Paid")
                }
            }
        }
    }
}

private fun formatDueDate(dueDateMillis: Long): String {
    if (dueDateMillis == 0L) return "Not set"
    val date = Date(dueDateMillis)
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(date)
}


