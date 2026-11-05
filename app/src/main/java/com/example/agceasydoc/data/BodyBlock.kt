package com.example.agceasydoc.data

import androidx.compose.ui.text.style.TextAlign

data class BodyBlock(
    val text: String = "",
    val fontSize: Float = 12f,
    val isBold: Boolean = false,
    val alignment: TextAlign = TextAlign.Justify
)
