package com.example.fitnesstrackerapp.data

import com.example.fitnesstrackerapp.R

// requirement of 10 items using LazyColumn in ActivitiesScreen
// Note: the METs in this sample data are derived from a website and other sources mentioned in the README.md
// Continued note: The METs are NOT 100% accurate
object SampleData {
    val activities = listOf(
        ActivityItem(
            name = "Jogging",
            description = "Steady 6–8km/h jog",
            met = 7.0,
            imageRes = R.drawable.jog),
        ActivityItem(
            name = "Cycling",
            description = "Casual outdoor cycling",
            met = 5.8,
            imageRes = R.drawable.bike),
        ActivityItem(
            name = "Walking",
            description = "Brisk walk 5km/h",
            met = 3.5,
            imageRes = R.drawable.walk),
        ActivityItem(
            name = "HIIT",
            description = "High intensity interval training",
            met = 10.0,
            imageRes = R.drawable.hiit),
        ActivityItem(
            name = "Yoga",
            description = "Hatha/stretching",
            met = 3.0,
            imageRes = R.drawable.yoga),
        ActivityItem(
            name = "Rowing",
            description = "Moderate pace focusing on back muscles",
            met = 7.0,
            imageRes = R.drawable.row),
        ActivityItem(name = "Swimming",
            description = "Freestyle at a moderate pace",
            met = 6.5,
            imageRes = R.drawable.swim),
        ActivityItem(name = "Boxing",
            description = "Bag work or sparring",
            met = 8.0,
            imageRes = R.drawable.box),
        ActivityItem(
            name = "Elliptical Machine",
            description = "Moderate resistance",
            met = 6.5,
            imageRes = R.drawable.elliptical),
        ActivityItem(
            name = "Stairmaster",
            description = "Moderate pace on stairmaster machine",
            met = 7.0,
            imageRes = R.drawable.stairs)
    )
}
