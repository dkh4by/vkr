package com.example.electronicreception.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val OfficialPrimary = Color(0xFF0F3D5E)
private val OfficialSecondary = Color(0xFF1F6F8B)
private val OfficialAccent = Color(0xFF2F9E7E)
private val OfficialBackground = Color(0xFFF4F7F9)
private val OfficialSurface = Color(0xFFFFFFFF)
private val OfficialError = Color(0xFFDC3545)

private val LightColors = lightColorScheme(
    primary = OfficialPrimary,
    secondary = OfficialSecondary,
    tertiary = OfficialAccent,
    background = OfficialBackground,
    surface = OfficialSurface,
    error = OfficialError,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1F2933),
    onSurface = Color(0xFF1F2933)
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp)
)

@Composable
fun ElectronicReceptionTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(),
        shapes = AppShapes,
        content = content
    )
}