package com.example.fit.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val Lime = Color(0xFFB8FF3B)
val FitBackground = Color(0xFF101114)
val FitSurface = Color(0xFF1C1F23)
val FitMuted = Color(0xFFA9ADB3)
private val FitColors = darkColorScheme(
    primary = Lime, onPrimary = FitBackground,
    primaryContainer = Color(0xFF29351D), onPrimaryContainer = Lime,
    background = FitBackground, onBackground = Color.White,
    surface = FitSurface, onSurface = Color.White,
    surfaceContainer = FitSurface, surfaceContainerLow = FitSurface,
    surfaceContainerHighest = Color(0xFF262A30), surfaceVariant = Color(0xFF262A30),
    onSurfaceVariant = FitMuted, secondary = FitMuted,
    outline = Color(0xFF42474E), outlineVariant = Color(0xFF30343A),
    error = Color(0xFFFF6565)
)
@Composable fun FitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FitColors, typography = Typography,
        shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(20.dp), large = RoundedCornerShape(24.dp)),
        content = content
    )
}
