package com.example.creativecommissionstracker.ui.orders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.creativecommissionstracker.data.FirebaseRepository
import com.example.creativecommissionstracker.model.Client
import com.example.creativecommissionstracker.model.Order

data class OrdersUiState(
    val isLoading: Boolean = true,
    val orders: List<Order> = emptyList(),
    val clients: List<Client> = emptyList()
)

class OrdersViewModel(
    private val repo: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    var uiState by mutableStateOf(OrdersUiState())
        private set

    init {
        refresh()
    }

    fun refresh() {
        uiState = uiState.copy(isLoading = true)

        // load clients first, then orders to ensure orders have client info
        repo.getClients { clients ->
            repo.getOrders { orders ->
                uiState = uiState.copy(
                    isLoading = false,
                    orders = orders,
                    clients = clients
                )
            }
        }
    }

    fun markOrderDone(orderId: String) {
        val current = uiState.orders.find { it.id == orderId } ?: return
        val updated = current.copy(status = "done")

        repo.addOrUpdateOrder(updated) {
            refresh()
        }

    }

    fun markOrderPaid(orderId: String) {
        val current = uiState.orders.find { it.id == orderId } ?: return
        val updated = current.copy(isPaid = true)

        repo.addOrUpdateOrder(updated) {
            refresh()
        }

    }
}



