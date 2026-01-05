package com.example.creativecommissionstracker.ui.orders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.creativecommissionstracker.data.FirebaseRepository
import com.example.creativecommissionstracker.model.Client
import com.example.creativecommissionstracker.model.Order

data class OrderDetailsUiState(
    val isLoading: Boolean = true,
    val order: Order? = null,
    val client: Client? = null
)

class OrderDetailsViewModel(
    private val repo: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    var uiState by mutableStateOf(OrderDetailsUiState())
        private set

    fun load(orderId: String) {
        uiState = uiState.copy(isLoading = true)

        repo.getOrderById(orderId) { order ->
            if (order == null) {
                uiState = uiState.copy(isLoading = false, order = null, client = null)
            }
            else {
                repo.getClients { clients ->
                    val client = clients.find { it.id == order.clientId }

                    uiState = uiState.copy(
                        isLoading = false,
                        order = order,
                        client = client
                    )

                }

            }
        }
    }

    fun markOrderDone() {
        val current = uiState.order ?: return
        val updated = current.copy(status = "done")

        repo.addOrUpdateOrder(updated) {
            uiState = uiState.copy(order = updated)
        }

    }

    fun markOrderPaid() {
        val current = uiState.order ?: return
        val updated = current.copy(isPaid = true)

        repo.addOrUpdateOrder(updated) {
            uiState = uiState.copy(order = updated)
        }

    }
}