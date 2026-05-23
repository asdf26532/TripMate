package com.han.tripmate.data.model

data class PackingItem(
    val id: String,
    val name: String,
    val category: String,
    val isPacked: Boolean = false
)