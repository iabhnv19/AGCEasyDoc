package com.example.agceasydoc.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AutoResizeText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int,
    textAlign: TextAlign = TextAlign.Center,
    style: TextStyle,
    minTextSize: TextUnit = 9.sp
) {
    var textSize by remember { mutableStateOf(style.fontSize) }
    var lineHeight by remember {
        mutableStateOf(style.lineHeight.takeIf { it != TextUnit.Unspecified }
            ?: (style.fontSize * 1.2))
    }

    Text(
        text = text,
        modifier = modifier,
        maxLines = maxLines,
        softWrap = true,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign,
        style = style.copy(
            fontSize = textSize,
            lineHeight = lineHeight
        ),
        onTextLayout = { result ->
            if (result.didOverflowHeight && textSize > minTextSize) {
                textSize *= 0.9f
                lineHeight *= 0.9f
            }
        }
    )
}