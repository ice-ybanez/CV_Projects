package com.example.creativecommissionstracker.ui.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.creativecommissionstracker.model.Order
import com.example.creativecommissionstracker.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import com.example.creativecommissionstracker.notifications.NotificationHelper


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderScreen(
    navController: NavHostController,
    vm: AddOrderViewModel = viewModel()
) {
    val state = vm.uiState

    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var artType by remember { mutableStateOf<String?>(null) }
    var medium by remember { mutableStateOf<String?>(null) }
    var style by remember { mutableStateOf<String?>(null) }
    var priceText by remember { mutableStateOf("") }
    var dueDateText by remember { mutableStateOf("") }
    var selectedClientId by remember { mutableStateOf<String?>(null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Order") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Back")
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
                }
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // art type
                        Text(
                            text = "Art Type",
                            fontWeight = FontWeight.SemiBold
                        )
                        ChoiceChipsRow(
                            options = listOf(
                                "contemporary" to "Contemporary",
                                "custom" to "Custom Art",
                                "portrait" to "Portrait"
                            ),
                            selected = artType,
                            onSelectedChange = { artType = it }
                        )

                        // medium
                        Text(
                            text = "Medium",
                            fontWeight = FontWeight.SemiBold
                        )
                        ChoiceChipsRow(
                            options = listOf(
                                "acrylic" to "Acrylic",
                                "oil" to "Oil"
                            ),
                            selected = medium,
                            onSelectedChange = { medium = it }
                        )

                        // style
                        Text(
                            text = "Style",
                            fontWeight = FontWeight.SemiBold
                        )
                        ChoiceChipsRow(
                            options = listOf(
                                "abstract" to "Abstract",
                                "pop" to "Pop",
                                "photorealistic" to "Photorealistic"
                            ),
                            selected = style,
                            onSelectedChange = { style = it }
                        )


                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Price (€)") },
                            modifier = Modifier.fillMaxWidth()
                        )


                        OutlinedTextField(
                            value = dueDateText,
                            onValueChange = { dueDateText = it },
                            label = { Text("Due date (dd/MM/yyyy)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // client selection
                        Text(
                            text = "Client",
                            fontWeight = FontWeight.SemiBold
                        )

                        if (state.clients.isEmpty()) {
                            Text("No clients yet. Add some in the Clients screen.")
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.clients) { client ->
                                    val selected = selectedClientId == client.id
                                    FilterChip(
                                        selected = selected,
                                        onClick = {
                                            selectedClientId =
                                                if (selected) null else client.id
                                        },
                                        label = { Text(client.name) }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // basic validation
                                if (title.isBlank()) {
                                    errorMessage = "Title is required."
                                    return@Button
                                }
                                if (artType == null) {
                                    errorMessage = "Art type is required."
                                    return@Button
                                }
                                if (medium == null) {
                                    errorMessage = "Medium is required."
                                    return@Button
                                }
                                if (style == null) {
                                    errorMessage = "Style is required."
                                    return@Button
                                }
                                if (selectedClientId == null) {
                                    errorMessage = "Please select a client."
                                    return@Button
                                }
                                val price = priceText.toDoubleOrNull()
                                if (price == null) {
                                    errorMessage = "Price must be a number."
                                    return@Button
                                }

                                val dueMillis = parseDueDateToMillis(dueDateText)

                                isSaving = true
                                errorMessage = null

                                val order = Order(
                                    id = "",
                                    clientId = selectedClientId!!,
                                    title = title.trim(),
                                    description = description.trim(),
                                    artType = artType!!,
                                    medium = medium!!,
                                    style = style!!,
                                    price = price,
                                    dueDate = dueMillis,
                                    status = "pending",
                                    isPaid = false
                                )

                                if(dueMillis > 0L) {
                                    NotificationHelper.scheduleDueDateNotification(
                                        context = context,
                                        orderTitle = order.title,
                                        dueDateMillis = dueMillis
                                    )
                                }

                                vm.createOrder(order) {
                                    isSaving = false
                                    // go back to Orders screen.. recreating it so it refreshes
                                    navController.navigate(Screen.Orders.route) {
                                        popUpTo(Screen.Orders.route) { inclusive = true }
                                    }
                                }
                            },
                            enabled = !isSaving && !state.isLoading,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(if (isSaving) "Saving..." else "Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChoiceChipsRow(
    options: List<Pair<String, String>>,
    selected: String?,
    onSelectedChange: (String?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { (value, label) ->
            val isSelected = selected == value
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        onSelectedChange(null)
                    } else {
                        onSelectedChange(value)
                    }
                },
                label = { Text(label) }
            )
        }
    }
}

private fun parseDueDateToMillis(text: String): Long {
    if (text.isBlank()) return 0L
    return try {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = format.parse(text)
        date?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}


