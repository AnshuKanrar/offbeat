package com.example.offbeat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.offbeat.R

val Cormorant = FontFamily(
    Font(
        R.font.cormorant_garamond_regular,
        FontWeight.Normal
    ),
    Font(
        R.font.cormorant_garamond_semibold,
        FontWeight.SemiBold
    )
)
val Lora = FontFamily(
    Font(
        R.font.lora_regular,
        FontWeight.Normal
    ),
    Font(
        R.font.lora_semibold,
        FontWeight.SemiBold
    )
)

private const val Figures = "lnum, tnum"

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Cormorant,
        fontWeight = FontWeight.Normal,
        fontSize = 64.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.02).em,
        fontFeatureSettings = Figures
    ),
    displayMedium = TextStyle(
        fontFamily = Cormorant, fontWeight = FontWeight.Normal,
        fontSize = 54.sp, lineHeight = 54.sp, letterSpacing = (-0.02).em
    ),
    displaySmall = TextStyle(
        fontFamily = Cormorant,
        fontWeight = FontWeight.Normal,
        fontSize = 46.sp,
        lineHeight = 46.sp,
        letterSpacing = (-0.015).em,
        fontFeatureSettings = Figures
    ),

    headlineLarge = TextStyle(
        fontFamily = Cormorant, fontWeight = FontWeight.Normal,
        fontSize = 42.sp, lineHeight = 44.sp, letterSpacing = (-0.015).em
    ),
    headlineMedium = TextStyle(
        fontFamily = Cormorant, fontWeight = FontWeight.Normal,
        fontSize = 38.sp, lineHeight = 40.sp, letterSpacing = (-0.015).em
    ),
    headlineSmall = TextStyle(
        fontFamily = Cormorant, fontWeight = FontWeight.Normal,
        fontSize = 28.sp, lineHeight = 30.sp, letterSpacing = (-0.01).em
    ),


    titleLarge = TextStyle(
        fontFamily = Cormorant, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 23.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Cormorant, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 21.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Cormorant, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 18.sp
    ),


    bodyLarge = TextStyle(
        fontFamily = Lora,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = Lora,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.5.sp
    ),

    bodySmall = TextStyle(
        fontFamily = Lora,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    labelLarge = TextStyle(
        fontFamily = Cormorant,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 20.sp
    ),

    labelMedium = TextStyle(
        fontFamily = Lora,
        fontWeight = FontWeight.Normal,
        fontSize = 11.5.sp,
        lineHeight = 14.sp
    ),

    labelSmall = TextStyle(
        fontFamily = Lora,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.12.em
    ) // use .uppercase()
)

val LyricsStyle = TextStyle(
    fontFamily = Cormorant, fontWeight = FontWeight.Normal,
    fontSize = 32.sp, lineHeight = 38.sp
)          // active line: SemiBold, gold; others 40–62% alpha
val ChartAxisStyle = TextStyle(fontFamily = Lora, fontSize = 10.5.sp, lineHeight = 12.sp)

