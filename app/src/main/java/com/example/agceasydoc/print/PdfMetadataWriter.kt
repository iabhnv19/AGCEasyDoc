package com.example.agceasydoc.print

import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.itextpdf.kernel.pdf.PdfDocument as ITextPdfDocument

suspend fun writePdfWithMetadata(
    bitmap: Bitmap,
    outputFile: File,
    documentTitle: String
) = withContext(Dispatchers.IO) {

    val timestamp = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
    ).format(Date())

    val rawPdfBytes = renderBitmapToPdfBytes(bitmap)
    val tempFile = File(outputFile.parent, "temp_${outputFile.name}")
    tempFile.writeBytes(rawPdfBytes)

    val reader   = PdfReader(tempFile)
    val writer   = PdfWriter(FileOutputStream(outputFile))
    val iTextDoc = ITextPdfDocument(reader, writer)

    val info = iTextDoc.documentInfo
    info.title   = documentTitle
    info.author  = "AGC EasyDoc"
    info.subject = "AGC EasyDoc document"
    info.creator = "AGC EasyDoc v1.9.5"
    info.setMoreInfo("GeneratedAt",  timestamp)
    info.setMoreInfo("Institution",  "Amritsar Group Of Colleges")

    iTextDoc.close()
    tempFile.delete()

}

private fun renderBitmapToPdfBytes(bitmap: Bitmap): ByteArray {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
    pdfDocument.finishPage(page)
    val stream = ByteArrayOutputStream()
    pdfDocument.writeTo(stream)
    pdfDocument.close()
    return stream.toByteArray()
}
