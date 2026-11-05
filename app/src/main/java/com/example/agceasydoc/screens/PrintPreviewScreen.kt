package com.example.agceasydoc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PrintPreviewScreen(
    document: DocumentData
) {

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEEE)),
        contentAlignment = Alignment.TopCenter
    ) {
        val pageWidth = maxWidth * 0.9f
        val pageHeight = pageWidth * 1.414f

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .width(pageWidth)
                    .height(pageHeight)
                    .background(Color.White)
                    .border(1.dp, Color.Black)
                    .padding(72.dp)
            ) {

                PrintHeader(
                    document,
                    renderMode = RenderMode.PRINT
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (document.bodyBlocks.isEmpty() ||
                    document.bodyBlocks.all { it.text.isBlank() }
                ) {
                    Text(
                        text = "Body content will appear here.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                } else {
                    document.bodyBlocks.forEach { block ->
                        Text(
                            text = block.text,
                            fontSize = block.fontSize.sp,
                            fontWeight = if (block.isBold) FontWeight.Bold else FontWeight.Normal,
                            textAlign = block.alignment,
                            lineHeight = (block.fontSize * 1.4f).sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
