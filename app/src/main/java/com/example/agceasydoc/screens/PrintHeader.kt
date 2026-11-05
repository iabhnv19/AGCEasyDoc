package com.example.agceasydoc.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agceasydoc.R

enum class RenderMode {
    SCREEN,
    PRINT
}

@Composable
fun PrintHeader(
    document: DocumentData,
    renderMode: RenderMode
) {
    val borderWidth = if (renderMode == RenderMode.PRINT) 0.8.dp else 1.dp
    val showRefOrDate = document.showRefNo || document.showDate

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (showRefOrDate) 104.dp else 90.dp)
            .border(BorderStroke(borderWidth, Color.Black))
    ) {

        Box(
            modifier = Modifier
                .weight(1.4f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.bn),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(0.85f)
            )
        }

        Column(
            modifier = Modifier
                .weight(1.6f)
                .fillMaxHeight()
                .border(BorderStroke(borderWidth, Color.Black))
                .padding(horizontal = 4.dp, vertical = 3.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true),
                contentAlignment = Alignment.Center
            ) {
                IndependentAutoSizeText(
                    text = document.title.ifBlank { "Title" },
                    maxLines = if (showRefOrDate) 4 else 6,
                    maxFontSize = 24.sp,
                    minFontSize = 6.sp,
                    renderMode = renderMode,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (showRefOrDate) {
                Divider(
                    thickness = if (renderMode == RenderMode.PRINT) 0.5.dp else 0.7.dp,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 1.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (document.showRefNo) {
                        Text(
                            text = "Ref. No.: ${document.refNo}",
                            fontSize = if (renderMode == RenderMode.PRINT) 8.sp else 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .padding(end = 4.dp)
                        )
                    }

                    if (document.showDate) {
                        Text(
                            text = "Date: ${document.date}",
                            fontSize = if (renderMode == RenderMode.PRINT) 8.sp else 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = if (!document.showRefNo) Modifier.fillMaxWidth() else Modifier
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            IndependentAutoSizeText(
                text = document.department.ifBlank { "Department / Club" },
                maxLines = 5,
                maxFontSize = 20.sp,
                minFontSize = 6.sp,
                renderMode = renderMode,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun IndependentAutoSizeText(
    text: String,
    maxLines: Int,
    maxFontSize: TextUnit,
    minFontSize: TextUnit,
    renderMode: RenderMode,
    style: TextStyle,
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier
) {
    val measurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = modifier) {
        val availableWidth = constraints.maxWidth
        val availableHeight = constraints.maxHeight

        val optimalFontSize = remember(
            text,
            maxLines,
            availableWidth,
            availableHeight,
            maxFontSize,
            minFontSize,
            renderMode
        ) {
            if (text.isBlank()) {
                minFontSize
            } else {
                calculateOptimalFontSize(
                    text = text,
                    maxLines = maxLines,
                    maxFontSize = maxFontSize,
                    minFontSize = minFontSize,
                    availableWidth = availableWidth,
                    availableHeight = availableHeight,
                    style = style,
                    measurer = measurer
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                textAlign = textAlign,
                style = style.copy(
                    fontSize = optimalFontSize,
                    lineHeight = (optimalFontSize.value * 1.2f).sp,
                    textAlign = textAlign
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }
    }
}

private fun calculateOptimalFontSize(
    text: String,
    maxLines: Int,
    maxFontSize: TextUnit,
    minFontSize: TextUnit,
    availableWidth: Int,
    availableHeight: Int,
    style: TextStyle,
    measurer: androidx.compose.ui.text.TextMeasurer
): TextUnit {
    var currentSize = maxFontSize.value
    val minSize = minFontSize.value
    val step = 0.5f

    while (currentSize >= minSize) {
        val lineHeight = (currentSize * 1.2f).sp

        val testStyle = style.copy(
            fontSize = currentSize.sp,
            lineHeight = lineHeight
        )

        val result = measurer.measure(
            text = AnnotatedString(text),
            style = testStyle,
            maxLines = maxLines,
            constraints = Constraints(maxWidth = availableWidth)
        )

        val widthFits = result.size.width <= availableWidth
        val heightFits = result.size.height <= availableHeight
        val noOverflow = !result.hasVisualOverflow

        if (widthFits && heightFits && noOverflow) {
            return currentSize.sp
        }

        currentSize -= step
    }
    return minSize.sp
}
