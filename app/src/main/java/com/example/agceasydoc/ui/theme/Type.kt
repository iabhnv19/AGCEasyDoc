package com.example.agceasydoc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.agceasydoc.R

val TimesFontFamily = FontFamily(
    Font(R.font.liberation_serif_regular, FontWeight.Normal),
    Font(R.font.liberation_serif_bold, FontWeight.Bold),
    Font(R.font.liberation_serif_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.liberation_serif_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = TimesFontFamily, fontSize = 24.sp),
    displayMedium = TextStyle(fontFamily = TimesFontFamily, fontSize = 20.sp),
    displaySmall = TextStyle(fontFamily = TimesFontFamily, fontSize = 18.sp),
    headlineLarge = TextStyle(fontFamily = TimesFontFamily, fontSize = 20.sp),
    headlineMedium = TextStyle(fontFamily = TimesFontFamily, fontSize = 18.sp),
    headlineSmall = TextStyle(fontFamily = TimesFontFamily, fontSize = 16.sp),
    titleLarge = TextStyle(fontFamily = TimesFontFamily, fontSize = 16.sp),
    titleMedium = TextStyle(fontFamily = TimesFontFamily, fontSize = 14.sp),
    titleSmall = TextStyle(fontFamily = TimesFontFamily, fontSize = 12.sp),
    bodyLarge = TextStyle(
        fontFamily = TimesFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(fontFamily = TimesFontFamily, fontSize = 12.sp),
    bodySmall = TextStyle(fontFamily = TimesFontFamily, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = TimesFontFamily, fontSize = 12.sp),
    labelMedium = TextStyle(fontFamily = TimesFontFamily, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = TimesFontFamily, fontSize = 12.sp)
)
