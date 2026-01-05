package com.example.creativecommissionstracker.model

data class Client(
    val id: String = "",
    val name: String = "",
    val socialHandle: String = "",
    val notes: String = ""
)

data class Order(
    val id: String = "",
    val clientId: String = "",
    val title: String = "",
    val description: String = "",
    val artType: String = "",   // "contemporary", "custom", "portrait"
    val medium: String = "",    // "acrylic", "oil"
    val style: String = "",     // "abstract", "pop", "photorealistic"
    val price: Double = 0.0,
    val dueDate: Long = 0L,     // stored as millis, couldn't get timestamp to work
    val status: String = "pending",
    val isPaid: Boolean = false
)

data class Artwork(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val artType: String = "",   // "contemporary", "custom", "portrait"
    val medium: String = "",    // "acrylic", "oil"
    val style: String = "",     // "abstract", "pop", "photorealistic"
    val price: Double = 0.0
)

data class Artist(
    val id: String = "",
    val name: String = "",
    val instagramUrl: String = ""
)