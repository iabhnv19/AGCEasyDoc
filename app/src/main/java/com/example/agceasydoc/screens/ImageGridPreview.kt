package com.example.agceasydoc.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agceasydoc.print.compressImage
import com.example.agceasydoc.print.getImageSizeInMB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

@Composable
fun ImageGridPreview(
    images: List<Uri>,
    onRemoveImage: (Int) -> Unit
) {
    val context = LocalContext.current
    var compressedBitmaps by remember(images) {
        mutableStateOf<List<Bitmap>>(emptyList())
    }
    var isLoading by remember(images) {
        mutableStateOf(true)
    }
    var loadingProgress by remember(images) {
        mutableStateOf(0f)
    }

    LaunchedEffect(images) {
        if (images.isEmpty()) {
            compressedBitmaps = emptyList()
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        withContext(Dispatchers.IO) {
            val bitmaps = images.mapIndexed { index, uri ->
                async {
                    loadingProgress = (index.toFloat() / images.size)

                    val sizeInMB = getImageSizeInMB(context, uri)

                    val (maxWidth, maxHeight, quality) = when {
                        sizeInMB > 10f -> Triple(800, 800, 70)
                        sizeInMB > 5f -> Triple(1000, 1000, 75)
                        sizeInMB > 2f -> Triple(1200, 1200, 80)
                        else -> Triple(1200, 1200, 85)
                    }

                    compressImage(
                        context = context,
                        uri = uri,
                        maxWidth = maxWidth,
                        maxHeight = maxHeight,
                        quality = quality
                    )
                }
            }.awaitAll()

            compressedBitmaps = bitmaps
            isLoading = false
        }
    }

    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                progress = loadingProgress,
                color = Color(0xFF2563EB)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Compressing images... ${(loadingProgress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280)
            )
        }
    } else if (compressedBitmaps.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            compressedBitmaps.chunked(2).forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEachIndexed { colIndex, bitmap ->
                        val imageIndex = rowIndex * 2 + colIndex

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        ) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Image ${imageIndex + 1}",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = { onRemoveImage(imageIndex) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(28.dp)
                                    .background(
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFE5E7EB),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = "${imageIndex + 1}",
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                                    .background(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }

                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
