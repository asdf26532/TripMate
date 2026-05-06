package com.han.tripmate.data.model

data class Itinerary(
    val id: String = "",
    val title: String = "",
    val time: String = "",
    val memo: String = "",
    val location: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)