package com.example.agceasydoc.data

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agceasydoc.R
import com.example.agceasydoc.screens.AutoResizeText
import com.example.agceasydoc.screens.DocumentData
import com.example.agceasydoc.screens.SignatureAlignment


@Composable
fun DocumentContent(
    document: DocumentData,
    bitmaps: List<Bitmap>
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(72.dp)
        ) {

            val showRefOrDate = document.showRefNo || document.showDate

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        if (showRefOrDate) 96.dp else 85.dp
                    )
                    .border(BorderStroke(1.dp, Color.Black))
            ) {

                Row(
                    modifier = Modifier.fillMaxSize() .weight(1f)
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1.4f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bn),
                            contentDescription = "College Logo",
                            modifier = Modifier
                                .padding(6.dp)
                                .fillMaxWidth(0.9f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight()
                            .border(BorderStroke(1.dp, Color.Black))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                AutoResizeText(
                                    text = document.title.ifBlank { "Title" },
                                    maxLines = if (showRefOrDate) 4 else 6,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }

                            if (showRefOrDate) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (document.showRefNo) {
                                        AutoResizeText(
                                            text = "Ref. No: ${document.refNo.ifBlank { "—" }}",
                                            maxLines = 1,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    if (document.showDate) {
                                        AutoResizeText(
                                            text = "Date: ${document.date.ifBlank { "—" }}",
                                            maxLines = 1,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AutoResizeText(
                            text = document.department.ifBlank { "Department / Club" },
                            maxLines = 5,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White)
            ) {
                document.bodyBlocks.forEach { block ->
                    if (block.text.isNotBlank()) {
                        Text(
                            text = block.text,
                            fontSize = block.fontSize.sp,
                            fontWeight = if (block.isBold) FontWeight.Bold else FontWeight.Normal,
                            textAlign = block.alignment,
                            lineHeight = (block.fontSize * 1.4f).sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))
                    }
                }


                if (bitmaps.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))

                    bitmaps.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { bmp ->
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }

                document.table?.let { table ->
                    Spacer(Modifier.height(24.dp))
                    DocumentTable(table)
                }

                if (document.signatures.isNotEmpty()) {

                    Spacer(modifier = Modifier.height(40.dp))

                    val signatures = document.signatures

                    if (
                        signatures.size == 2 &&
                        signatures.any { it.alignment == SignatureAlignment.LEFT } &&
                        signatures.any { it.alignment == SignatureAlignment.RIGHT }
                    ) {

                        val leftSignature =
                            signatures.first { it.alignment == SignatureAlignment.LEFT }
                        val rightSignature =
                            signatures.first { it.alignment == SignatureAlignment.RIGHT }

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = leftSignature.text,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Medium,
                                fontSize = document.signatureFontSize.sp
                            )

                            Text(
                                text = rightSignature.text,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Medium,
                                fontSize = document.signatureFontSize.sp
                            )
                        }

                    } else {
                        signatures.forEach { signature ->
                            Text(
                                text = signature.text,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = when (signature.alignment) {
                                    SignatureAlignment.LEFT -> TextAlign.Start
                                    SignatureAlignment.CENTER -> TextAlign.Center
                                    SignatureAlignment.RIGHT -> TextAlign.End
                                },
                                fontWeight = FontWeight.Medium,
                                fontSize = document.signatureFontSize.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentTable(table: TableData) {

    Column {

        if (table.title.isNotBlank()) {
            Text(
                text = table.title,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Column(
            modifier = Modifier.border(1.dp, Color.Black)
        ) {
            table.cells.forEach { row ->
                Row {
                    row.forEach { cell ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(0.5.dp, Color.Black)
                                .padding(8.dp)
                        ) {
                            Text(cell)
                        }
                    }
                }
            }
        }
    }
}
