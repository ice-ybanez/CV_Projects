package com.example.creativecommissionstracker.ui.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.creativecommissionstracker.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    navController: NavHostController,
    orderId: String,
    vm: OrderDetailsViewModel = viewModel()
) {

    val state = vm.uiState

    LaunchedEffect(orderId) {
        vm.load(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.order == null -> {
                    Text(
                        text = "Order not found.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {


                    val order = state.order

                    OrderDetailsContent(
                        order = order,
                        clientName = state.client?.name ?: "Unknown client",
                        onMarkDone = { vm.markOrderDone() },
                        onMarkPaid = { vm.markOrderPaid() }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderDetailsContent(
    order: Order,
    clientName: String,
    onMarkDone: () -> Unit,
    onMarkPaid: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = order.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Client: $clientName",
            style = MaterialTheme.typography.bodyMedium
        )

        if (order.description.isNotBlank()) {
            Text(
                text = "Description: ${order.description}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Art type: ${order.artType}",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Medium: ${order.medium}",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Style: ${order.style}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Price: €${order.price.toInt()}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Due date: ${formatDueDate(order.dueDate)}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Status: ${order.status.replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = if (order.isPaid) "Paid" else "Not paid",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(16.dp))

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

private fun formatDueDate(dueDateMillis: Long): String {
    if (dueDateMillis == 0L) return "Not set"

    val date = Date(dueDateMillis)
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    return formatter.format(date)
}



