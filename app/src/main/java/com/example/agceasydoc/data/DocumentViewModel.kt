package com.example.agceasydoc.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agceasydoc.screens.DocumentData
import com.example.agceasydoc.screens.SignatureAlignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocumentViewModel : ViewModel() {

    private companion object {
        const val TAG = "DocumentViewModel"
    }

    var document = mutableStateOf(
        DocumentData(
            title = "",
            department = "",
            bodyBlocks = listOf(BodyBlock()),
            signatures = emptyList(),
            images = emptyList(),
            table = null,
            signatureFontSize = 12f
        )
    )
        private set

    var currentDocumentId = mutableStateOf<Long?>(null)
        private set

    fun updateTitle(value: String) {
        document.value = document.value.copy(title = value)
    }

    fun updateDepartment(value: String) {
        document.value = document.value.copy(department = value)
    }

    fun toggleRefNo(enabled: Boolean) {
        document.value = document.value.copy(showRefNo = enabled)
    }

    fun toggleDate(enabled: Boolean) {
        document.value = document.value.copy(showDate = enabled)
    }

    fun updateRefNo(value: String) {
        document.value = document.value.copy(refNo = value)
    }

    fun updateDate(value: String) {
        document.value = document.value.copy(date = value)
    }


    fun updateBodyBlock(index: Int, block: BodyBlock) {
        val blocks = document.value.bodyBlocks.toMutableList()
        if (index !in blocks.indices) return

        blocks[index] = block
        document.value = document.value.copy(bodyBlocks = blocks)
    }

    fun addBodyBlock() {
        document.value = document.value.copy(
            bodyBlocks = document.value.bodyBlocks + BodyBlock()
        )
    }

    fun removeBodyBlock(index: Int) {
        val blocks = document.value.bodyBlocks.toMutableList()
        if (blocks.size <= 1 || index !in blocks.indices) return

        blocks.removeAt(index)
        document.value = document.value.copy(bodyBlocks = blocks)
    }

    fun addSignature() {
        document.value = document.value.copy(
            signatures = document.value.signatures + SignatureBlock()
        )
    }

    fun updateSignature(index: Int, block: SignatureBlock) {
        val list = document.value.signatures.toMutableList()
        if (index !in list.indices) return

        list[index] = block
        document.value = document.value.copy(signatures = list)
    }

    fun removeSignature(index: Int) {
        val list = document.value.signatures.toMutableList()
        if (index !in list.indices) return

        list.removeAt(index)
        document.value = document.value.copy(signatures = list)
    }

    fun updateImages(newImages: List<Uri>) {
        document.value = document.value.copy(images = newImages)
    }

    fun removeImage(index: Int) {
        val images = document.value.images.toMutableList()
        if (index !in images.indices) return

        images.removeAt(index)
        document.value = document.value.copy(images = images)
    }

    fun updateTable(table: TableData?) {
        document.value = document.value.copy(table = table)
    }

    fun updateSubtitle(value: String) {
        document.value = document.value.copy(subtitle = value)
    }

    fun updateSignatureFontSize(size: Float) {
        document.value = document.value.copy(signatureFontSize = size)
    }

    fun saveDocument(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val dao = db.documentDao()

                val doc = document.value
                val bodyBlocksJson = Json.encodeToString(
                    doc.bodyBlocks.map { it.toData() }
                )
                val imageUrisJson = Json.encodeToString(
                    doc.images.map { it.toString() }
                )
                val tableJson = doc.table?.let { Json.encodeToString(it.toSerialized()) }
                val signaturesJson = Json.encodeToString(
                    doc.signatures.map { it.toData() }
                )

                val savedDoc = SavedDocument(
                    id = currentDocumentId.value ?: 0,
                    title = doc.title.ifBlank { "Untitled Document" },
                    department = doc.department,
                    showRefNo = doc.showRefNo,
                    showDate = doc.showDate,
                    refNo = doc.refNo,
                    date = doc.date,
                    subtitle = doc.subtitle,
                    bodyBlocksJson = bodyBlocksJson,
                    imageUrisJson = imageUrisJson,
                    tableJson = tableJson,
                    signaturesJson = signaturesJson,
                    signatureFontSize = doc.signatureFontSize,
                    lastModified = System.currentTimeMillis()
                )

                val id = dao.insertDocument(savedDoc)
                currentDocumentId.value = id
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving document", e)
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun loadDocument(context: Context, documentId: Long, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val dao = db.documentDao()

                val savedDoc = dao.getDocumentById(documentId)

                if (savedDoc == null) {
                    Log.e(TAG, "Document not found for id: $documentId")
                    return@launch
                }

                val bodyBlocks = Json.decodeFromString<List<BodyBlockData>>(
                    savedDoc.bodyBlocksJson
                ).map { it.toBodyBlock() }

                val imageUris = Json.decodeFromString<List<String>>(
                    savedDoc.imageUrisJson
                ).map { Uri.parse(it) }

                val table = savedDoc.tableJson?.let {
                    Json.decodeFromString<TableDataSerialized>(it).toTableData()
                }

                val signatures = Json.decodeFromString<List<SignatureBlockData>>(
                    savedDoc.signaturesJson
                ).map { it.toSignatureBlock() }

                withContext(Dispatchers.Main) {
                    document.value = DocumentData(
                        title = savedDoc.title,
                        department = savedDoc.department,
                        showRefNo = savedDoc.showRefNo,
                        showDate = savedDoc.showDate,
                        refNo = savedDoc.refNo,
                        date = savedDoc.date,
                        subtitle = savedDoc.subtitle,
                        bodyBlocks = bodyBlocks,
                        images = imageUris,
                        table = table,
                        signatures = signatures,
                        signatureFontSize = savedDoc.signatureFontSize
                    )
                    currentDocumentId.value = documentId

                    onComplete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading document", e)
            }
        }
    }

    fun createNewDocument() {
        document.value = DocumentData(
            title = "",
            department = "",
            bodyBlocks = listOf(BodyBlock()),
            signatures = emptyList(),
            images = emptyList(),
            table = null,
            showRefNo = false,
            showDate = false,
            signatureFontSize = 12f
        )
        currentDocumentId.value = null
    }

    fun createAiGeneratedDocument(title: String, department: String) {
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val refNumber = "AGC/${department.take(4).uppercase()}/${System.currentTimeMillis().toString().takeLast(4)}"
        
        val lowerTitle = title.lowercase()
        val bodyBlocks = mutableListOf<BodyBlock>()
        val signatures = mutableListOf<SignatureBlock>()

        when {
            lowerTitle.contains("workshop") || lowerTitle.contains("training") || lowerTitle.contains("hands-on") -> {
                bodyBlocks.add(BodyBlock(text = "It is hereby notified to all students and faculty of the $department that a hands-on technical workshop titled \"$title\" has been organized.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "Objective & Scope: The workshop aims to bridge the gap between theoretical knowledge and practical industry applications. Participants will engage in interactive lab sessions, tool demonstrations, and guided project work.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "Schedule & Venue: All registered participants are required to report to the designated department lab 15 minutes prior to commencement with their required equipment.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "Certificates of completion will be awarded to attendees upon successful submission of feedback and project tasks.", fontSize = 12f))
                
                signatures.add(SignatureBlock(text = "Workshop Coordinator\n$department", alignment = SignatureAlignment.LEFT))
                signatures.add(SignatureBlock(text = "Head of Department\n$department", alignment = SignatureAlignment.RIGHT))
            }
            lowerTitle.contains("seminar") || lowerTitle.contains("lecture") || lowerTitle.contains("guest") || lowerTitle.contains("talk") -> {
                bodyBlocks.add(BodyBlock(text = "This is to inform all members of the $department that an expert guest lecture / seminar on \"$title\" is scheduled to be held shortly.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "Distinguished Speaker: An industry expert / renowned academician has been invited to share cutting-edge insights, current industry trends, and career guidance.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "Interactive Q&A: Attendees will have the opportunity to interact directly with the speaker during the dedicated question-and-answer segment.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "All faculty members and students are cordially invited to attend and benefit from this enriching academic session.", fontSize = 12f))
                
                signatures.add(SignatureBlock(text = "Faculty In-Charge\n$department", alignment = SignatureAlignment.LEFT))
                signatures.add(SignatureBlock(text = "Head of Department\n$department", alignment = SignatureAlignment.RIGHT))
            }
            lowerTitle.contains("exam") || lowerTitle.contains("test") || lowerTitle.contains("assessment") || lowerTitle.contains("quiz") -> {
                bodyBlocks.add(BodyBlock(text = "NOTICE: Important Examination Guidelines regarding \"$title\" for students of the $department.", fontSize = 12f, isBold = true))
                bodyBlocks.add(BodyBlock(text = "Timing & Punctuality: Students must occupy their respective examination halls 10 minutes before the commencement time. Latecomers exceeding 15 minutes will not be permitted.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "Required Items: Students must carry their college identity cards and necessary stationery. The use of unauthorized electronic devices is strictly prohibited.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "Seating arrangements and hall allocations have been posted on the department notice board.", fontSize = 12f))
                
                signatures.add(SignatureBlock(text = "Examination Coordinator\n$department", alignment = SignatureAlignment.LEFT))
                signatures.add(SignatureBlock(text = "Controller of Examinations", alignment = SignatureAlignment.RIGHT))
            }
            lowerTitle.contains("meeting") || lowerTitle.contains("review") || lowerTitle.contains("committee") -> {
                bodyBlocks.add(BodyBlock(text = "CIRCULAR: Meeting Notice for $department", fontSize = 12f, isBold = true))
                bodyBlocks.add(BodyBlock(text = "Agenda: A meeting regarding \"$title\" has been scheduled to discuss administrative updates, academic planning, and committee action items.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "All committee members and designated faculty are requested to attend the meeting punctually at the designated venue.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "Prior submissions of progress reports or agenda inputs must reach the coordinator's desk beforehand.", fontSize = 12f))
                
                signatures.add(SignatureBlock(text = "Member Secretary\n$department", alignment = SignatureAlignment.LEFT))
                signatures.add(SignatureBlock(text = "Chairperson / HOD\n$department", alignment = SignatureAlignment.RIGHT))
            }
            lowerTitle.contains("hackathon") || lowerTitle.contains("competition") || lowerTitle.contains("coding") || lowerTitle.contains("contest") -> {
                bodyBlocks.add(BodyBlock(text = "ANNOUNCEMENT: Inter-Departmental Competition / Hackathon - \"$title\"", fontSize = 12f, isBold = true))
                bodyBlocks.add(BodyBlock(text = "Event Overview: Designed to foster innovation, rapid prototyping, and creative problem-solving among students of the $department.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "Team Guidelines & Submissions: Teams comprising 2-4 members must register via the portal. Projects will be evaluated on originality, technical complexity, and presentation.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "Exciting prizes, certificates, and recognition await the winning entries.", fontSize = 12f))
                
                signatures.add(SignatureBlock(text = "Event Lead\n$department", alignment = SignatureAlignment.LEFT))
                signatures.add(SignatureBlock(text = "Head of Department\n$department", alignment = SignatureAlignment.RIGHT))
            }
            else -> {
                bodyBlocks.add(BodyBlock(text = "It is hereby notified to all faculty members, staff, and students of the $department that an official academic program / notice regarding \"$title\" has been issued.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "The primary objective of this initiative is to ensure smooth academic functioning, institutional growth, and active stakeholder participation.", fontSize = 12f))
                bodyBlocks.add(BodyBlock(text = "All concerned individuals are requested to take note of the guidelines and cooperate accordingly. For clarifications, contact the department office.", fontSize = 12f))
                
                signatures.add(SignatureBlock(text = "Coordinator\n$department", alignment = SignatureAlignment.LEFT))
                signatures.add(SignatureBlock(text = "Head of Department\n$department", alignment = SignatureAlignment.RIGHT))
            }
        }

        document.value = DocumentData(
            title = title,
            department = department,
            showRefNo = true,
            showDate = true,
            refNo = refNumber,
            date = currentDate,
            bodyBlocks = bodyBlocks,
            signatures = signatures,
            signatureFontSize = 12f
        )
        currentDocumentId.value = null
    }

}
