package com.han.tripmate.ui.theme

import androidx.compose.ui.graphics.Color

data class CategoryStyle(
    val icon: String,
    val color: Color
) {
    companion object {

        val Default = CategoryStyle("💰", Color(0xFF78909C))

        fun fromCategory(category: String?): CategoryStyle {
            if (category.isNullOrBlank()) return Default

            return when (category.trim()) {
                "식비", "맛집", "카페" -> CategoryStyle("🍔", Color(0xFFFF7043))
                "숙박", "호텔", "펜션" -> CategoryStyle("🏨", Color(0xFF2692F1))
                "교통", "항공", "택시", "주유" -> CategoryStyle("🚗", Color(0xFF26A69A))
                "쇼핑", "기념품" -> CategoryStyle("🛍️", Color(0xFFAB47BC))
                "관광", "액티비티", "입장료" -> CategoryStyle("🎟️", Color(0xFFFFB300))
                else -> Default
            }
        }
    }
}