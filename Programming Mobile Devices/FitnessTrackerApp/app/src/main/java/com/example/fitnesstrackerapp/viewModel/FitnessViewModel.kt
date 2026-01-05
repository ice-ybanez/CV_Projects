package com.example.fitnesstrackerapp.viewModel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fitnesstrackerapp.data.ActivityItem
import com.example.fitnesstrackerapp.data.LogEntry
import com.example.fitnesstrackerapp.data.SampleData
import kotlin.math.roundToInt

// shared ViewModel across all screens
// this holds the item list and any state like filters as required
// holds item list and saved logs that survive config changes
class FitnessViewModel : ViewModel() {

    // source of truth for item list
    val activities = mutableStateListOf<ActivityItem>().apply {
        addAll(SampleData.activities)
    }

    // mutable in-memory list of saved logs from Screen 3
    val logs = mutableStateListOf<LogEntry>()

    // search query for filtering
    var searchQuery by mutableStateOf("")

    // One-off UI message for toasts, shows when saving and cleared after shown
    var uiMessage by mutableStateOf<String?>(null)
        private set

    // looks up activity by its id
    fun activityById(id: String): ActivityItem? =
        activities.firstOrNull { it.id == id }

    // called from Screen 2 or from Screen 3 FAB
    // saves user input to logs
    // meets requirement: saves input in a shared, lifecycle-aware ViewModel
    fun addLog(
        activity: ActivityItem,
        durationMin: Int,
        intensity: Int,
        note: String
    ) {
        val calories = estimateCalories(activity.met, durationMin, intensity)
        logs.add(
            LogEntry(
                activityId = activity.id,
                title = activity.name,
                durationMin = durationMin,
                intensity = intensity,
                note = note,
                calories = calories
            )
        )
        uiMessage = "Saved ${activity.name} • $calories kcal"
    }

    fun consumeMessage() { uiMessage = null }   // called after toast is shown

    // selected category, in this case activity, to filter by; null = show all
    var selectedCategoryId: String? by mutableStateOf(null)
        private set

    // call this from the UI to apply/clear the filter
    fun setCategoryFilter(activityId: String?) {
        selectedCategoryId = activityId
    }

    // list the UI should display - filter by category and search query
    // uses Compose snapshot state, so it updates instantly and survives rotation.
    val visibleLogs by derivedStateOf {
        val q = searchQuery.trim().lowercase()
        val cat = selectedCategoryId
        logs.filter { log ->
            val matchesCategory = (cat == null) || (log.activityId == cat)
            val matchesQuery = q.isEmpty() ||
                    log.title.lowercase().contains(q) ||
                    log.note.lowercase().contains(q)
            matchesCategory && matchesQuery
        }
    }

    private fun estimateCalories(met: Double, durationMin: Int, intensity: Int): Int {
        // formula: Calories/min ≈ MET * 3.5 * weight(kg) / 200
        // Scale slightly by intensity (1..10 -> 0.8..1.2)
        val userWeightKg = 70.0
        val intensityFactor = 0.8 + (intensity - 1) * (0.4 / 9.0)
        val perMin = met * 3.5 * userWeightKg / 200.0 * intensityFactor
        return (perMin * durationMin).roundToInt()
    }
}
