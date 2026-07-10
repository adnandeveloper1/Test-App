package com.nexappra.testapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BrandYellow,
    onPrimary = MainTextColor,
    secondary = BrandYellowDark,
    background = ScreenBackground,
    surface = CardBackground,
    onSurface = MainTextColor,
    onBackground = MainTextColor,
    error = ErrorRed
)

@Composable
fun TestAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
