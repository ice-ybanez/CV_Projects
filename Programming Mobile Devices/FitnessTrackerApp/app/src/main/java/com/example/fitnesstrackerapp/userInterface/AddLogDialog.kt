package com.example.fitnesstrackerapp.userInterface

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.fitnesstrackerapp.data.ActivityItem
import com.example.fitnesstrackerapp.viewModel.FitnessViewModel

// Dialog used by the FAB in Screen 3
@Composable
fun AddLogDialog(
    onDismiss: () -> Unit,
    onSave: (ActivityItem, Int, Int, String) -> Unit,
    vm: FitnessViewModel
) {
    val activities = vm.activities

    // keep only selectedId in saveable state for rotation safety
    var selectedId by rememberSaveable {
        mutableStateOf(activities.firstOrNull()?.id.orEmpty())
    }
    val selected = activities.firstOrNull { it.id == selectedId }

    var minutes by rememberSaveable {mutableStateOf("30") }
    var intensity by rememberSaveable { mutableFloatStateOf(5f) }
    var note by rememberSaveable { mutableStateOf("") }

    // dropdown anchored to text field
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    // for dropdown
    val density = LocalDensity.current
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrowRotation"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val act = selected ?: return@TextButton
                    val m = minutes.toIntOrNull() ?: 0
                    val i = intensity.toInt().coerceIn(1, 10)
                    onSave(act, m, i, note)
                },
                enabled = selected != null && (minutes.toIntOrNull() ?: 0) > 0
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Quick Add Log") },
        text = {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(scrollState), // added fix: now note can be added by scrolling when rotated
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // activity dropdown
                Box {
                    OutlinedTextField(
                        value = selected?.name ?: "",
                        onValueChange = {},     // read-only, can choose from dropdown
                        readOnly = true,
                        label = { Text("Activity") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    Icons.Filled.ArrowDropDown,
                                    contentDescription = "Choose activity",
                                    modifier = Modifier.rotate(arrowRotation)   // animation for dropdown to rotate up
                                )
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coords ->
                                val size = coords.size
                                textFieldSize = Size(size.width.toFloat(),
                                    size.height.toFloat()
                                )
                            }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.width(with(density) {
                            textFieldSize.width.toDp()
                        })
                    ) {
                        activities.forEach { a ->
                            DropdownMenuItem(
                                text = { Text(a.name) },
                                onClick = {
                                    selectedId = a.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = minutes,
                    onValueChange = { minutes = it.filter(Char::isDigit) },
                    label = { Text("Duration (min)") },
                    singleLine = true
                )

                Column {
                    Text("Intensity: ${intensity.toInt()}")
                    Slider(
                        value = intensity,
                        onValueChange = { intensity = it },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
