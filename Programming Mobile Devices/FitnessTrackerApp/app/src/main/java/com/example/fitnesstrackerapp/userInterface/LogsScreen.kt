package com.example.fitnesstrackerapp.userInterface

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnesstrackerapp.userInterface.AddLogDialog
import com.example.fitnesstrackerapp.viewModel.FitnessViewModel

// Screen 3: Logs
@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun LogsScreen(vm: FitnessViewModel) {
    // state backed in ViewModel
    val logs = vm.visibleLogs                  // filtered by category and search query by ViewModel
    val query = vm.searchQuery
    val activities = vm.activities             // categories come from Screen 1 activities
    val selectedCatId = vm.selectedCategoryId

    // persists config changes
    var showAdd by rememberSaveable { mutableStateOf(false) }
    var filterMenuOpen by remember { mutableStateOf(false) }

    // for the filter button label "All"
    val selectedLabel = activities.firstOrNull { it.id == selectedCatId }?.name ?: "All"

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add log")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // filter menu and search bar in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // box for filter menu that has options for all categories and each item in list
                Box {
                    OutlinedButton(onClick = { filterMenuOpen = true }) {
                        Text("Filter: $selectedLabel")
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Choose category")
                    }
                    DropdownMenu(
                        expanded = filterMenuOpen,
                        onDismissRequest = { filterMenuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All") },
                            onClick = {
                                vm.setCategoryFilter(null)    // clears filter
                                filterMenuOpen = false        // closes filter menu
                            }
                        )
                        activities.forEach { a ->
                            DropdownMenuItem(
                                text = { Text(a.name) },
                                onClick = {
                                    vm.setCategoryFilter(a.id) // set filter to this category
                                    filterMenuOpen = false
                                }
                            )
                        }
                    }
                }

                // search textfield
                OutlinedTextField(
                    value = query,
                    onValueChange = { vm.searchQuery = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Search logs") },
                    singleLine = true
                )
            }

            // animated list
            Crossfade(
                targetState = selectedCatId,   // fade when applying/clearing category filter
                label = "logsFilterCrossfade"
            ) {
                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 24.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("No logs match this filter") }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(logs, key = { it.id }) { log ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 4.dp)
                            ) {
                                Text(log.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "${log.durationMin} min - Intensity ${log.intensity} - ${log.calories} kcal",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (log.note.isNotBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(log.note, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }

    // FAB dialog
    if (showAdd) {
        AddLogDialog(
            vm = vm,
            onDismiss = { showAdd = false },
            onSave = { activity, minutes, intensity, note ->
                vm.addLog(activity, minutes, intensity, note)
                showAdd = false
            }
        )
    }
}
