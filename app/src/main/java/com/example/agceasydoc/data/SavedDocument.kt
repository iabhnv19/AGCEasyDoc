package com.example.agceasydoc.data

import androidx.compose.ui.text.style.TextAlign
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agceasydoc.screens.SignatureAlignment
import kotlinx.serialization.Serializable

@Entity(tableName = "saved_documents")
data class SavedDocument(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val department: String,
    val showRefNo: Boolean,
    val showDate: Boolean,
    val refNo: String,
    val date: String,
    val subtitle: String,
    val bodyBlocksJson: String,
    val imageUrisJson: String,
    val tableJson: String?,
    val signaturesJson: String,
    val signatureFontSize: Float,
    val lastModified: Long = System.currentTimeMillis()
)

@Serializable
data class BodyBlockData(
    val text: String,
    val fontSize: Float,
    val isBold: Boolean,
    val alignment: String
)

@Serializable
data class SignatureBlockData(
    val text: String,
    val alignment: String
)

@Serializable
data class TableDataSerialized(
    val title: String,
    val rows: Int,
    val columns: Int,
    val cells: List<List<String>>
)

fun BodyBlock.toData() = BodyBlockData(
    text = text,
    fontSize = fontSize,
    isBold = isBold,
    alignment = when (alignment) {
        TextAlign.Start -> "Start"
        TextAlign.Center -> "Center"
        TextAlign.End -> "End"
        TextAlign.Justify -> "Justify"
        else -> "Justify"
    }
)

fun BodyBlockData.toBodyBlock() = BodyBlock(
    text = text,
    fontSize = fontSize,
    isBold = isBold,
    alignment = when (alignment) {
        "Start" -> TextAlign.Start
        "Center" -> TextAlign.Center
        "End" -> TextAlign.End
        else -> TextAlign.Justify
    }
)

fun SignatureBlock.toData() = SignatureBlockData(
    text = text,
    alignment = alignment.name
)

fun SignatureBlockData.toSignatureBlock() = SignatureBlock(
    text = text,
    alignment = SignatureAlignment.valueOf(alignment)
)

fun TableData.toSerialized() = TableDataSerialized(
    title = title,
    rows = rows,
    columns = columns,
    cells = cells
)

fun TableDataSerialized.toTableData() = TableData(
    title = title,
    rows = rows,
    columns = columns,
    cells = cells
)
