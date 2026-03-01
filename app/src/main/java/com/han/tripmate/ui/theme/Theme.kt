package com.han.tripmate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 다크 모드용 색상 (다크 모드 지원할 때 수정)
private val DarkColorScheme = darkColorScheme(
    primary = MainBlue,
    secondary = SubBlue,
    tertiary = White,
    background = DarkGrey,
    surface = DarkGrey,
    onPrimary = White,
    onSecondary = DarkGrey
)

// 라이트 모드용 색상 (기본)
private val LightColorScheme = lightColorScheme(
    primary = MainBlue,         // 메인 블루
    secondary = SubBlue,        // 연한 블루
    tertiary = DarkGrey,        // 텍스트 등
    background = LightGrey,     // 전체 배경
    surface = White,            // 카드나 버튼 배경
    onPrimary = White,          // 메인 블루 위 글자색
    onSecondary = DarkGrey,     // 연한 블루 위 글자색
    onBackground = DarkGrey,    // 배경 위 글자색
    onSurface = DarkGrey        // 표면 위 글자색
)

@Composable
fun TripMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 다이나믹 컬러(기기 배경색 맞춤). 일단 false
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}