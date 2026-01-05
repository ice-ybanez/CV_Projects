package com.example.creativecommissionstracker.ui.clients

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.creativecommissionstracker.data.FirebaseRepository
import com.example.creativecommissionstracker.model.Client

data class ClientsUiState(
    val isLoading: Boolean = true,
    val clients: List<Client> = emptyList()
)

class ClientsViewModel(
    private val repo: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ClientsUiState())
        private set

    init {
        refresh()
    }

    fun refresh() {
        uiState = uiState.copy(isLoading = true)

        repo.getClients { clients ->

            uiState = uiState.copy(
                isLoading = false,
                clients = clients
            )

        }
    }

    fun addClient(
        name: String,
        socialHandle: String,
        notes: String,
        onDone: () -> Unit
    ) {
        val client = Client(
            id = "",
            name = name,
            socialHandle = socialHandle,
            notes = notes
        )
        repo.addClient(client) {
            refresh()
            onDone()
        }
    }
}


