package com.example.fitnesstrackerapp.data

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import java.util.UUID

// represents a selectable fitness activity shown in Screen 1 (Item List)
// Immutable to make it easier to share across composables
@Immutable
data class ActivityItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val met: Double,    // metabolic equivalent of task value used to estimate calories
    @DrawableRes val imageRes: Int? = null  // for images from res/drawable
)

// represents a log entry shown in Screen 3 (Logs) created from Screen 2 or Screen 3 FAB
@Immutable
data class LogEntry(
    val id: String = UUID.randomUUID().toString(),
    val activityId: String,     // ties back to ActivityItem
    val title: String,
    val durationMin: Int,
    val intensity: Int,         // input from 1-10
    val note: String,
    val calories: Int,          // calculated value stored at save time
    val timestamp: Long = System.currentTimeMillis()
)
