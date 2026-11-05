package com.example.agceasydoc.screens

import android.net.Uri
import com.example.agceasydoc.data.BodyBlock
import com.example.agceasydoc.data.SignatureBlock
import com.example.agceasydoc.data.TableData

data class DocumentData(
    val title: String = "",
    val department: String = "",
    val bodyBlocks: List<BodyBlock> = listOf(BodyBlock()),
    val signatures: List<SignatureBlock> = emptyList(),
    val images: List<Uri> = emptyList(),
    val table: TableData? = null,
    val showRefNo: Boolean = false,
    val showDate: Boolean = false,
    val refNo: String = "",
    val date: String = "",
    val subtitle: String = "",
    val signatureFontSize: Float = 12f
)

enum class SignatureAlignment {
    LEFT, CENTER, RIGHT
}
