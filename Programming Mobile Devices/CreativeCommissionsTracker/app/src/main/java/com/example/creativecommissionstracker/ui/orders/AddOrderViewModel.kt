package com.example.creativecommissionstracker.ui.orders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.creativecommissionstracker.data.FirebaseRepository
import com.example.creativecommissionstracker.model.Client
import com.example.creativecommissionstracker.model.Order

data class AddOrderUiState(
    val isLoading: Boolean = true,
    val clients: List<Client> = emptyList()
)

class AddOrderViewModel(
    private val repo: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    var uiState by mutableStateOf(AddOrderUiState())
        private set

    init {
        loadClients()
    }

    private fun loadClients() {

        uiState = uiState.copy(isLoading = true)

        repo.getClients { clients ->
            uiState = uiState.copy(
                isLoading = false,
                clients = clients
            )
        }
    }

    fun createOrder(order: Order, onDone: () -> Unit) {
        repo.addOrUpdateOrder(order) {
            onDone()
        }
    }
}