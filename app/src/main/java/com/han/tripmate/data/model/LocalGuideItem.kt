package com.han.tripmate.data.model

data class LocalGuideItem(
    val name: String,
    val location: String,
    val tag: String,
    val rating: Double,
    val reviewCount: Int,
    val profileUrl: String
)
