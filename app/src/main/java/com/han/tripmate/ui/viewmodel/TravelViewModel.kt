package com.han.tripmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.han.tripmate.data.TravelService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TravelViewModel : ViewModel() {
    // 실제로는 여기서 서버 데이터를 받아오겠지만, 지금은 임시 데이터를 담아둡니다.
    private val _services = MutableStateFlow(listOf(
        TravelService(
            id = "1", authorId = "expert_01",
            title = "파리 비즈니스 전문 통역 및 VIP 의전 가이드 (벤츠 제공)",
            location = "프랑스 파리", category = "비즈니스/통역",
            price = 45000, priceUnit = "시간당",
            rating = 4.9, reviewCount = 128,
            thumbnailUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34", // 파리 예시 사진
            isVerified = true, tags = listOf("통역전문", "차량지원")
        ),
        TravelService(
            id = "2", authorId = "expert_02",
            title = "도쿄 아키하바라 피규어&애니 투어 (현지인 단골샵 방문)",
            location = "일본 도쿄", category = "테마투어",
            price = 28000, priceUnit = "시간당",
            rating = 4.8, reviewCount = 56,
            thumbnailUrl = "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf", // 도쿄 예시 사진
            tags = listOf("덕질여행", "맛집탐방")
        ),
        TravelService(
            id = "3", authorId = "expert_03",
            title = "다낭 미케비치 서핑 레슨 및 로컬 스냅 촬영",
            location = "베트남 다낭", category = "액티비티",
            price = 35000, priceUnit = "1회당",
            rating = 5.0, reviewCount = 42,
            thumbnailUrl = "https://images.unsplash.com/photo-1559592413-7ece3593e103", // 다낭 예시 사진
            isVerified = true, tags = listOf("인생샷", "초보환영")
        )
    ))
    val services: StateFlow<List<TravelService>> = _services.asStateFlow()

    // 찜한 아이템 ID들을 저장하는 Set
    private val _favoriteIds = MutableStateFlow(setOf<String>())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    // 찜하기 토글 함수
    fun toggleFavorite(serviceId: String) {
        val current = _favoriteIds.value
        if (current.contains(serviceId)) {
            _favoriteIds.value = current - serviceId
        } else {
            _favoriteIds.value = current + serviceId
        }
    }
}