# ✈️ [TripMate] - 현지 가이드 매칭 플랫폼

> "현지 거주자와 여행자를 연결하는 신뢰 기반의 여행 상품 직거래 서비스"

---

## 📌 프로젝트 소개
- **목표**: 기존 패키지 여행의 틀을 벗어나, 현지에 거주하는 한국인(유학생, 거주자)과 여행자를 직접 매칭해주는 플랫폼입니다.
- **핵심 가치**: 당근마켓의 '동네 인증' 시스템을 벤치마킹하여, 검증된 현지인의 생생한 가이드를 제공합니다.
- **개발 기간**: 2026.02 ~ (진행 중)

## 🛠 Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: Clean Architecture + MVVM
- **Async**: Coroutines + Flow
- **DI**: Hilt
- **Backend**: Firebase (Auth, Firestore, Storage, Cloud Messaging)

## ✨ 주요 기능
1. **가이드 탐색**: 지역별/테마별 현지 가이드 리스트 조회 (Google Maps 연동)
2. **실시간 채팅**: 가이드와 여행자 간의 일정 및 가격 협의 (Firebase Realtime DB)
3. **가이드 등록**: 현지 거주 증빙을 통한 가이드 권한 획득 시스템
4. **리뷰 및 평점**: 신뢰도 기반의 사용자 피드백 시스템
