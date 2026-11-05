package com.example.agceasydoc.print

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

suspend fun compressImage(
    context: Context,
    uri: Uri,
    maxWidth: Int = 1200,
    maxHeight: Int = 1200,
    quality: Int = 85
): Bitmap = withContext(Dispatchers.IO) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Cannot open input stream")

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565

        val decodedStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Cannot open input stream")

        var bitmap = BitmapFactory.decodeStream(decodedStream, null, options)
            ?: throw IOException("Failed to decode bitmap")
        decodedStream.close()

        bitmap = handleImageRotation(context, uri, bitmap)

        if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
            val ratio = minOf(
                maxWidth.toFloat() / bitmap.width,
                maxHeight.toFloat() / bitmap.height
            )
            val newWidth = (bitmap.width * ratio).toInt()
            val newHeight = (bitmap.height * ratio).toInt()

            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            if (scaledBitmap != bitmap) {
                bitmap.recycle()
            }
            bitmap = scaledBitmap
        }

        bitmap
    } catch (e: Exception) {
        Bitmap.createBitmap(400, 400, Bitmap.Config.RGB_565).apply {
            eraseColor(android.graphics.Color.LTGRAY)
        }
    }
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while ((halfHeight / inSampleSize) >= reqHeight &&
            (halfWidth / inSampleSize) >= reqWidth
        ) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

private fun handleImageRotation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
    try {
        val orientation = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exif = ExifInterface(inputStream)
            inputStream.close()
            exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } else {
            val tempFile = File.createTempFile("exif_temp", ".jpg", context.cacheDir)
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val exif = ExifInterface(tempFile.absolutePath)
            val orient = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            tempFile.delete()
            orient
        }

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )

        if (rotatedBitmap != bitmap) {
            bitmap.recycle()
        }

        return rotatedBitmap
    } catch (e: Exception) {
        return bitmap
    }
}

fun getImageSizeInMB(context: Context, uri: Uri): Float {
    return try {
        context.contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
            val sizeInBytes = descriptor.statSize
            sizeInBytes / (1024f * 1024f)
        } ?: 0f
    } catch (e: Exception) {
        0f
    }
}
