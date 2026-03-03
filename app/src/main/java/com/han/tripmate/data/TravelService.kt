package com.han.tripmate.data

data class TravelService(
    val id: String,                 // 고유 아이디
    val authorId: String,           // 작성자 ID
    val title: String,              // 서비스 제목
    val location: String,           // 지역
    val category: String,           // 카테고리 (통역, 투어, 의전 등)
    val price: Long,                // 가격
    val priceUnit: String,          // 단위 (시간당, 1회당)
    val rating: Double,             // 평점
    val reviewCount: Int,           // 리뷰 개수
    val thumbnailUrl: String,       // 목록용 대표 이미지 URL
    val images: List<String> = emptyList(), // 상세 페이지용 전체 이미지 URL 리스트
    val isVerified: Boolean = false, // 인증 여부
    val tags: List<String> = emptyList()
)

