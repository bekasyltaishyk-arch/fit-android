package com.example.fit.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun fitStyle(size: Int, line: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = FontFamily.SansSerif, fontWeight = weight,
    fontSize = size.sp, lineHeight = line.sp, letterSpacing = 0.sp
)
val Typography = Typography(
    displaySmall = fitStyle(36, 42, FontWeight.Bold),
    headlineLarge = fitStyle(30, 36, FontWeight.Bold),
    headlineMedium = fitStyle(26, 32, FontWeight.Bold),
    headlineSmall = fitStyle(23, 29, FontWeight.Bold),
    titleLarge = fitStyle(20, 26, FontWeight.SemiBold),
    titleMedium = fitStyle(16, 22, FontWeight.SemiBold),
    titleSmall = fitStyle(14, 20, FontWeight.SemiBold),
    bodyLarge = fitStyle(16, 24), bodyMedium = fitStyle(14, 21), bodySmall = fitStyle(12, 18),
    labelLarge = fitStyle(14, 20, FontWeight.SemiBold),
    labelMedium = fitStyle(12, 18, FontWeight.Medium), labelSmall = fitStyle(11, 16, FontWeight.Medium)
)
