package com.example.agceasydoc.screens

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agceasydoc.R
import com.example.agceasydoc.data.DocumentContent
import com.example.agceasydoc.data.TableData
import com.example.agceasydoc.print.compressImage
import com.example.agceasydoc.print.renderComposableToBitmap
import com.example.agceasydoc.print.writePdfWithMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun DocumentPreview(
    document: DocumentData,
    images: List<Uri>
) {
    val context = LocalContext.current

    var compressedImages by remember(images) { mutableStateOf<List<Bitmap>>(emptyList()) }
    var imagesReady by remember { mutableStateOf(false) }

    LaunchedEffect(images) {
        imagesReady = false
        compressedImages = if (images.isEmpty()) {
            emptyList()
        } else {
            images.map { uri -> compressImage(context, uri) }
        }
        imagesReady = true
    }

    Column {
        DocumentContent(
            document = document,
            bitmaps = compressedImages
        )
        Spacer(modifier = Modifier.height(24.dp))
        PrintButton(
            document = document,
            bitmaps = compressedImages,
            enabled = imagesReady
        )
    }
}

@Composable
fun PrintButton(
    document: DocumentData,
    bitmaps: List<Bitmap>,
    enabled: Boolean
) {
    val context = LocalContext.current
    val activity = context.findActivity() as? ComponentActivity ?: return

    var isPreparing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            enabled = enabled && !isPreparing,
            onClick = {
                scope.launch {
                    isPreparing = true

                    try {
                        val dpi = 200
                        val a4WidthMm = 210f
                        val a4HeightMm = 297f
                        val mmToInch = 0.0393701f
                        val width = (a4WidthMm * mmToInch * dpi).toInt()
                        val height = (a4HeightMm * mmToInch * dpi).toInt()

                        val bitmap = withContext(Dispatchers.Main) {
                            renderComposableToBitmap(
                                activity = activity,
                                width = width,
                                height = height
                            ) {
                                PrintableDocument(document = document, bitmaps = bitmaps)
                            }
                        }

                        withContext(Dispatchers.IO) {
                            val fileName = "EasyDoc_${System.currentTimeMillis()}.pdf"

                            val tempFile = File(context.cacheDir, fileName)

                            writePdfWithMetadata(
                                bitmap        = bitmap,
                                outputFile    = tempFile,
                                documentTitle = document.title.ifBlank { "AGC EasyDoc Document" }
                            )

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                val contentValues = android.content.ContentValues().apply {
                                    put(android.provider.MediaStore.Downloads.DISPLAY_NAME, fileName)
                                    put(android.provider.MediaStore.Downloads.MIME_TYPE, "application/pdf")
                                    put(android.provider.MediaStore.Downloads.IS_PENDING, 1)
                                }

                                val resolver = context.contentResolver
                                val uri = resolver.insert(
                                    android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                    contentValues
                                )

                                uri?.let {
                                    resolver.openOutputStream(it)?.use { outputStream ->
                                        tempFile.inputStream().copyTo(outputStream)
                                    }
                                    contentValues.clear()
                                    contentValues.put(android.provider.MediaStore.Downloads.IS_PENDING, 0)
                                    resolver.update(it, contentValues, null, null)
                                }
                            } else {
                                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                                if (!downloadsDir.exists()) {
                                    downloadsDir.mkdirs()
                                }
                                val destFile = File(downloadsDir, fileName)
                                tempFile.inputStream().use { input ->
                                    destFile.outputStream().use { output ->
                                        input.copyTo(output)
                                    }
                                }
                            }

                            tempFile.delete()
                        }

                        withContext(Dispatchers.Main) {
                            val printAttributes = PrintAttributes.Builder()
                                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 200, 200))
                                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                                .build()

                            printBitmapWithA4(
                                activity      = activity,
                                jobName       = "AGC EasyDoc",
                                bitmap        = bitmap,
                                attributes    = printAttributes,
                                documentTitle = document.title.ifBlank { "AGC EasyDoc Document" }
                            )
                        }

                    } catch (e: Exception) {
                        android.util.Log.e("PrintButton", "Export failed: ${e.message}", e)
                        withContext(Dispatchers.Main) {
                            android.widget.Toast.makeText(
                                context,
                                "Export failed: ${e.message}",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    } finally {
                        isPreparing = false
                    }
                }
            },
            modifier = Modifier.width(190.dp).height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB),
                disabledContainerColor = Color(0xFF94A3B8)
            )
        ) {
            if (isPreparing || !enabled) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("Preparing…", color = Color.White)
                }
            } else {
                Text(
                    text = "Done",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

fun printBitmapWithA4(
    activity: ComponentActivity,
    jobName: String,
    bitmap: Bitmap,
    attributes: PrintAttributes,
    documentTitle: String
) {
    val printManager = activity.getSystemService(Context.PRINT_SERVICE) as PrintManager

    val printAdapter = object : PrintDocumentAdapter() {
        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes?,
            cancellationSignal: CancellationSignal?,
            callback: LayoutResultCallback?,
            extras: Bundle?
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback?.onLayoutCancelled()
                return
            }
            val info = PrintDocumentInfo.Builder(jobName)
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build()
            callback?.onLayoutFinished(info, newAttributes != oldAttributes)
        }

        override fun onWrite(
            pages: Array<out PageRange>?,
            destination: ParcelFileDescriptor?,
            cancellationSignal: CancellationSignal?,
            callback: WriteResultCallback?
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback?.onWriteCancelled()
                return
            }
            try {
                val rawPdfBytes = run {
                    val doc = PdfDocument()
                    val pageInfo = PdfDocument.PageInfo.Builder(
                        bitmap.width, bitmap.height, 1
                    ).create()
                    val page = doc.startPage(pageInfo)
                    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                    doc.finishPage(page)
                    val stream = java.io.ByteArrayOutputStream()
                    doc.writeTo(stream)
                    doc.close()
                    stream.toByteArray()
                }

                val timestamp = java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    java.util.Locale.getDefault()
                ).format(java.util.Date())
                val inputStream = java.io.ByteArrayInputStream(rawPdfBytes)

                val reader = com.itextpdf.kernel.pdf.PdfReader(inputStream)
                val writer = com.itextpdf.kernel.pdf.PdfWriter(
                    java.io.FileOutputStream(destination!!.fileDescriptor)
                )
                val iTextDoc = com.itextpdf.kernel.pdf.PdfDocument(reader, writer)

                val info = iTextDoc.documentInfo
                info.title   = documentTitle
                info.author  = "AGC EasyDoc"
                info.subject = "AGC EasyDoc document"
                info.creator = "AGC EasyDoc v1.9.5"
                info.setMoreInfo("GeneratedAt", timestamp)
                info.setMoreInfo("Institution", "Amritsar Group Of Colleges")
                iTextDoc.close()
                callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))

            } catch (e: Exception) {
                callback?.onWriteFailed(e.message)
            }
        }
    }

    printManager.print(jobName, printAdapter, attributes)
}

@Composable
fun PrintableDocument(
    document: DocumentData,
    bitmaps: List<Bitmap>
) {
    val timesFontFamily = FontFamily(
        Font(R.font.liberation_serif_regular, FontWeight.Normal),
        Font(R.font.liberation_serif_bold, FontWeight.Bold),
        Font(R.font.liberation_serif_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.liberation_serif_bold_italic, FontWeight.Bold, FontStyle.Italic)
    )

    val defaultTypography = MaterialTheme.typography
    val timesTypography = Typography(
        displayLarge    = defaultTypography.displayLarge.copy(fontFamily = timesFontFamily),
        displayMedium   = defaultTypography.displayMedium.copy(fontFamily = timesFontFamily),
        displaySmall    = defaultTypography.displaySmall.copy(fontFamily = timesFontFamily),
        headlineLarge   = defaultTypography.headlineLarge.copy(fontFamily = timesFontFamily),
        headlineMedium  = defaultTypography.headlineMedium.copy(fontFamily = timesFontFamily),
        headlineSmall   = defaultTypography.headlineSmall.copy(fontFamily = timesFontFamily),
        titleLarge      = defaultTypography.titleLarge.copy(fontFamily = timesFontFamily),
        titleMedium     = defaultTypography.titleMedium.copy(fontFamily = timesFontFamily),
        titleSmall      = defaultTypography.titleSmall.copy(fontFamily = timesFontFamily),
        bodyLarge       = defaultTypography.bodyLarge.copy(fontFamily = timesFontFamily),
        bodyMedium      = defaultTypography.bodyMedium.copy(fontFamily = timesFontFamily),
        bodySmall       = defaultTypography.bodySmall.copy(fontFamily = timesFontFamily),
        labelLarge      = defaultTypography.labelLarge.copy(fontFamily = timesFontFamily),
        labelMedium     = defaultTypography.labelMedium.copy(fontFamily = timesFontFamily),
        labelSmall      = defaultTypography.labelSmall.copy(fontFamily = timesFontFamily)
    )

    MaterialTheme(typography = timesTypography) {
        ProvideTextStyle(value = TextStyle(fontFamily = timesFontFamily)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(72.dp)
            ) {
                PrintHeader(
                    document = document,
                    renderMode = RenderMode.PRINT
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (document.bodyBlocks.isNotEmpty() &&
                    document.bodyBlocks.any { it.text.isNotBlank() }
                ) {
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

                if (bitmaps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))

                    bitmaps.chunked(2).forEach { rowBitmaps ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowBitmaps.forEach { bmp ->
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            if (rowBitmaps.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                document.table?.let { table ->
                    Spacer(modifier = Modifier.height(24.dp))
                    PrintableTable(table)
                }

                if (document.signatures.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(40.dp))

                    val signatures = document.signatures
                    val leftSig  = signatures.firstOrNull { it.alignment == SignatureAlignment.LEFT }
                    val rightSig = signatures.firstOrNull { it.alignment == SignatureAlignment.RIGHT }

                    if (signatures.size == 2 && leftSig != null && rightSig != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PrintableSignature(
                                text = leftSig.text,
                                alignment = TextAlign.Start,
                                fontSize = document.signatureFontSize
                            )
                            PrintableSignature(
                                text = rightSig.text,
                                alignment = TextAlign.End,
                                fontSize = document.signatureFontSize
                            )
                        }
                    } else {
                        signatures.forEach { signature ->
                            PrintableSignature(
                                text = signature.text,
                                alignment = when (signature.alignment) {
                                    SignatureAlignment.LEFT   -> TextAlign.Start
                                    SignatureAlignment.CENTER -> TextAlign.Center
                                    SignatureAlignment.RIGHT  -> TextAlign.End
                                },
                                fontSize = document.signatureFontSize
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
private fun PrintableTable(table: TableData) {
    Column(modifier = Modifier.fillMaxWidth()) {

        if (table.title.isNotBlank()) {
            Text(
                text = table.title,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (table.cells.isEmpty()) return@Column
        val columnCount = table.cells.maxOf { it.size }
        val columnWeights = (0 until columnCount).map { colIndex ->
            table.cells
                .mapNotNull { row -> row.getOrNull(colIndex)?.length?.toFloat() }
                .maxOrNull()
                ?.coerceAtLeast(4f)
                ?: 4f
        }

        Column(modifier = Modifier.border(1.dp, Color.Black)) {
            table.cells.forEachIndexed { rowIndex, row ->
                val isHeader = rowIndex == 0

                Row(modifier = Modifier.fillMaxWidth()) {
                    (0 until columnCount).forEach { colIndex ->
                        val cell = row.getOrNull(colIndex) ?: ""
                        val isNumeric = cell.all { it.isDigit() || it == '+' || it == '-' }

                        Box(
                            modifier = Modifier
                                .weight(columnWeights[colIndex])
                                .border(0.5.dp, Color.Black)
                                .background(Color.White)
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cell,
                                fontSize = 10.sp,
                                fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                softWrap = !isHeader && !isNumeric,
                                maxLines = if (isHeader || isNumeric) 1 else Int.MAX_VALUE,
                                overflow = if (isHeader || isNumeric)
                                    androidx.compose.ui.text.style.TextOverflow.Visible
                                else
                                    androidx.compose.ui.text.style.TextOverflow.Clip
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrintableSignature(
    text: String,
    alignment: TextAlign,
    fontSize: Float
) {

    Column(
        horizontalAlignment = when (alignment) {
            TextAlign.Start  -> Alignment.Start
            TextAlign.Center -> Alignment.CenterHorizontally
            TextAlign.End    -> Alignment.End
            else             -> Alignment.CenterHorizontally
        }
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Medium,
            textAlign = alignment
        )
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
