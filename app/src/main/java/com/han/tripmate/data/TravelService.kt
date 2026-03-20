package com.han.tripmate.data

import com.google.firebase.Timestamp

data class TravelService(
    val id: String = "",
    val authorId: String = "",
    val title: String = "",
    val location: String = "",
    val category: String = "",
    val price: Long = 0L,
    val priceUnit: String = "시간당",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val thumbnailUrl: String = "",
    val images: List<String> = emptyList(),
    val isVerified: Boolean = false,
    val tags: List<String> = emptyList(),
    val createdAt: Timestamp? = null
)

