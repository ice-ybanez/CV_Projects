package com.example.fitnesstrackerapp.userInterface

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fitnesstrackerapp.viewModel.FitnessViewModel
import androidx.compose.material.icons.filled.ArrowBack

// Screen 2: Item Details
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    activityId: String?,
    onBack: () -> Unit,
    vm: FitnessViewModel
) {
    val act = remember(activityId) {
        activityId?.let(vm::activityById)
    }
    val focus = LocalFocusManager.current

    // user inputs with rememberSaveable to persist across config changes
    var minutes by rememberSaveable { mutableStateOf("30") }
    var intensity by rememberSaveable { mutableFloatStateOf(5f) }
    var note by rememberSaveable { mutableStateOf("") }

    // side effect of calories calculation
    var previewCalories by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(
        minutes,
        intensity,
        act?.met)
    {
        val m = minutes.toIntOrNull()

        // use same calculation as ViewModel for a quick on-screen preview (not saved)
        previewCalories = if (m != null && act != null) {
            val userWeightKg = 70.0
            val intensityFactor = 0.8 + (intensity.toInt() - 1) * (0.4 / 9.0)
            val perMin = act.met * 3.5 * userWeightKg / 200.0 * intensityFactor
            (perMin * m).toInt()
        }
        else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(act?.name?:"Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (act == null) {
            // ID missing or not found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center) {
                Text("Activity not found")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())  // added fix: screen is scrollable when rotated and remembers inputs
                .padding(16.dp)
                .animateContentSize(),      // small motion
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // image fitting the height to keep everything on screen
            if (act.imageRes != null) {
                Image(
                    painter = painterResource(id = act.imageRes),
                    contentDescription = act.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Text(act.description,
                style = MaterialTheme.typography.bodyLarge)

            OutlinedTextField(
                value = minutes,
                onValueChange = {
                    if (it.length <= 3) minutes = it.filter {
                        c -> c.isDigit()
                    }
                                },
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
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (previewCalories != null) {
                AssistChip(
                    onClick = {},
                    label = {Text("Estimated: ${previewCalories} kcal")}
                )
            }

            Button(
                onClick = {
                    // save to shared ViewModel
                    focus.clearFocus()
                    val m = minutes.toIntOrNull() ?: 0
                    val int = intensity.toInt().coerceIn(1, 10)
                    vm.addLog(act, durationMin = m, intensity = int, note = note)
                    onBack()
                },
                enabled = minutes.toIntOrNull()?.let { it > 0 } == true
            ) {
                Text("Save")
            }
        }
    }
}
