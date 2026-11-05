package com.example.agceasydoc.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.agceasydoc.data.DocumentViewModel
import com.example.agceasydoc.data.emptyTable

@Composable
fun CustomizeDocScreen(
    viewModel: DocumentViewModel
) {
    val context = LocalContext.current
    var isHeaderExpanded by remember { mutableStateOf(true) }
    var isBodyExpanded by remember { mutableStateOf(false) }
    var isSignatureExpanded by remember { mutableStateOf(false) }
    val document = viewModel.document.value
    var isImagesExpanded by remember { mutableStateOf(false) }
    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.updateImages(
                    (document.images + uris).take(4)
                )
            }
        }
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF7F7F7)),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
                .padding(top = 39.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Customize Document",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Button(
                onClick = {
                    if (isSaving) return@Button
                    isSaving = true
                    viewModel.saveDocument(
                        context = context,
                        onSuccess = {
                            isSaving = false
                            Toast.makeText(context, "Document saved!", Toast.LENGTH_SHORT).show()
                        },
                        onError = { error ->
                            isSaving = false
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00B341)
                )
            ) {
                Text(if (isSaving) "Saving..." else "Save", fontWeight = FontWeight.SemiBold)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),

        ) {
            Column(Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isHeaderExpanded = !isHeaderExpanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Header",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (isHeaderExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        null
                    )
                }

                AnimatedVisibility(isHeaderExpanded) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = document.title,
                            onValueChange = viewModel::updateTitle,
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedLabelColor = Color(0xFF2563EB),
                                cursorColor = Color(0xFF2563EB)
                            )
                        )

                        OutlinedTextField(
                            value = document.department,
                            onValueChange = viewModel::updateDepartment,
                            label = { Text("Department / Club") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedLabelColor = Color(0xFF2563EB),
                                cursorColor = Color(0xFF2563EB)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Reference Number",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = document.showRefNo,
                        onCheckedChange = viewModel::toggleRefNo,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2563EB),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1),
                            checkedBorderColor = Color(0xFF2563EB),
                            uncheckedBorderColor = Color(0xFFCBD5E1)
                        )
                    )
                }

                AnimatedVisibility(document.showRefNo) {
                    OutlinedTextField(
                        value = document.refNo,
                        onValueChange = viewModel::updateRefNo,
                        label = { Text("Reference Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = blueOutlinedColors()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Date",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = document.showDate,
                        onCheckedChange = viewModel::toggleDate,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2563EB),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1),
                            checkedBorderColor = Color(0xFF2563EB),
                            uncheckedBorderColor = Color(0xFFCBD5E1)
                        )
                    )
                }

                AnimatedVisibility(document.showDate) {
                    OutlinedTextField(
                        value = document.date,
                        onValueChange = viewModel::updateDate,
                        label = { Text("Date (DD/MM/YYYY)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = blueOutlinedColors()
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
        ) {
            Column(Modifier.padding(16.dp)) {

                SectionHeader("Body", isBodyExpanded) {
                    isBodyExpanded = !isBodyExpanded
                }

                AnimatedVisibility(isBodyExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

                        document.bodyBlocks.forEachIndexed { index, block ->

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                                OutlinedTextField(
                                    value = block.text,
                                    onValueChange = {
                                        viewModel.updateBodyBlock(
                                            index,
                                            block.copy(text = it)
                                        )
                                    },
                                    label = { Text("Body ${index + 1}") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = blueOutlinedColors()
                                )

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    FilterChip(
                                        selected = block.isBold,
                                        onClick = {
                                            viewModel.updateBodyBlock(
                                                index,
                                                block.copy(isBold = !block.isBold)
                                            )
                                        },
                                        label = { Text("Bold") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF2563EB),
                                            selectedLabelColor = Color.White,
                                            containerColor = Color.Transparent,
                                            labelColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    )

                                    listOf(
                                        TextAlign.Start to "Left",
                                        TextAlign.Center to "Center",
                                        TextAlign.End to "Right",
                                        TextAlign.Justify to "Justify"
                                    ).forEach { (align, label) ->
                                        FilterChip(
                                            selected = block.alignment == align,
                                            onClick = {
                                                viewModel.updateBodyBlock(
                                                    index,
                                                    block.copy(alignment = align)
                                                )
                                            },
                                            label = { Text(label) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF2563EB),
                                                selectedLabelColor = Color.White,
                                                containerColor = Color.Transparent,
                                                labelColor = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }

                                Text("Font size: ${block.fontSize.toInt()}")

                                Slider(
                                    value = block.fontSize,
                                    onValueChange = {
                                        viewModel.updateBodyBlock(
                                            index,
                                            block.copy(fontSize = it)
                                        )
                                    },
                                    valueRange = 12f..24f,
                                    colors = SliderDefaults.colors( thumbColor = Color(0xFF2563EB),
                                        activeTrackColor = Color(0xFF2563EB),
                                        inactiveTrackColor = Color(0xFF2563EB).copy(alpha = 0.3f) )
                                )

                                if (document.bodyBlocks.size > 1) {
                                    TextButton(
                                        onClick = { viewModel.removeBodyBlock(index) },
                                        colors = ButtonDefaults.textButtonColors( contentColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Remove section")
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = viewModel::addBodyBlock,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Text("Add Body Section")
                        }
                    }
                }
            }
        }


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isImagesExpanded = !isImagesExpanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Images",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (isImagesExpanded)
                            Icons.Default.ExpandLess
                        else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }

                AnimatedVisibility(isImagesExpanded) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = {
                                    imagePickerLauncher.launch(arrayOf("image/*"))
                                },
                                enabled = document.images.size < 4,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2563EB)
                                )
                            ) {
                                Text("Add Image (${document.images.size}/4)")
                            }
                        }

                        ImageGridPreview(
                            images = document.images,
                            onRemoveImage = viewModel::removeImage
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {

                val table = document.table
                val showTable = table != null

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Table",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = showTable,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                viewModel.updateTable(emptyTable(2, 2))
                            } else {
                                viewModel.updateTable(null)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2563EB),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1),
                            checkedBorderColor = Color(0xFF2563EB),
                            uncheckedBorderColor = Color(0xFFCBD5E1)
                        )
                    )
                }
                AnimatedVisibility(visible = showTable) {

                    table?.let { safeTable ->

                        var rowInput by remember(safeTable.rows) {
                            mutableStateOf(safeTable.rows.toString())
                        }
                        var colInput by remember(safeTable.columns) {
                            mutableStateOf(safeTable.columns.toString())
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = safeTable.title,
                                onValueChange = {
                                    viewModel.updateTable(safeTable.copy(title = it))
                                },
                                label = { Text("Table Title (optional)") },
                                shape = RoundedCornerShape(16.dp),
                                colors = blueOutlinedColors()
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                                OutlinedTextField(
                                    value = rowInput,
                                    onValueChange = { new ->
                                        if (new.isEmpty() || new.all { it.isDigit() }) {
                                            rowInput = new
                                            new.toIntOrNull()?.takeIf { it > 0 }?.let { r ->
                                                viewModel.updateTable(
                                                    emptyTable(r, safeTable.columns)
                                                        .copy(title = safeTable.title)
                                                )
                                            }
                                        }
                                    },
                                    label = { Text("Rows") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = blueOutlinedColors()
                                )

                                OutlinedTextField(
                                    value = colInput,
                                    onValueChange = { new ->
                                        if (new.isEmpty() || new.all { it.isDigit() }) {
                                            colInput = new
                                            new.toIntOrNull()?.takeIf { it > 0 }?.let { c ->
                                                viewModel.updateTable(
                                                    emptyTable(safeTable.rows, c)
                                                        .copy(title = safeTable.title)
                                                )
                                            }
                                        }
                                    },
                                    label = { Text("Columns") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = blueOutlinedColors()
                                )
                            }
                            safeTable.cells.forEachIndexed { r, row ->
                                Row {
                                    row.forEachIndexed { c, cell ->
                                        OutlinedTextField(
                                            value = cell,
                                            onValueChange = { new ->
                                                val updatedCells =
                                                    safeTable.cells.mapIndexed { ri, rr ->
                                                        rr.mapIndexed { ci, cc ->
                                                            if (ri == r && ci == c) new else cc
                                                        }
                                                    }
                                                viewModel.updateTable(
                                                    safeTable.copy(cells = updatedCells)
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(4.dp),
                                            singleLine = true,
                                            shape = RoundedCornerShape(16.dp),
                                            colors = blueOutlinedColors()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                val signatures = document.signatures

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isSignatureExpanded = !isSignatureExpanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Signatures",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = if (isSignatureExpanded)
                            Icons.Default.ExpandLess
                        else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }

                AnimatedVisibility(visible = isSignatureExpanded) {

                    Column {

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Font size: ${document.signatureFontSize.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF374151)
                        )

                        Spacer(modifier = Modifier.height(9.dp))

                        Slider(
                            value = document.signatureFontSize,
                            onValueChange = { viewModel.updateSignatureFontSize(it) },
                            valueRange = 10f..20f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF2563EB),
                                activeTrackColor = Color(0xFF2563EB),
                                inactiveTrackColor = Color(0xFF2563EB).copy(alpha = 0.3f)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = viewModel::addSignature,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Add")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        signatures.forEachIndexed { index, signature ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF9F9F9)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    OutlinedTextField(
                                        value = signature.text,
                                        onValueChange = {
                                            viewModel.updateSignature(
                                                index,
                                                signature.copy(text = it)
                                            )
                                        },
                                        label = { Text("Authority (e.g. Dean)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = blueOutlinedColors()
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        SignatureAlignment.values().forEach { align ->
                                            FilterChip(
                                                selected = signature.alignment == align,
                                                onClick = {
                                                    viewModel.updateSignature(
                                                        index,
                                                        signature.copy(alignment = align)
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        align.name.lowercase()
                                                            .replaceFirstChar { it.uppercase() }
                                                    )
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Color(0xFF2563EB),
                                                    selectedLabelColor = Color.White,
                                                    containerColor = Color.Transparent,
                                                    labelColor = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                    }

                                    TextButton(
                                        onClick = { viewModel.removeSignature(index) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error,
                                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
                                        )
                                    ) {
                                        Text("Remove")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            "Template Preview",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        DocumentPreview(
            document = viewModel.document.value,
            images = viewModel.document.value.images
        )

        Spacer(modifier = Modifier.height(34.dp))
    }
}


@Composable
private fun SectionHeader(
    title: String,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null
        )
    }
}

@Composable
private fun blueOutlinedColors() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF2563EB),
        unfocusedBorderColor = Color(0xFFCBD5E1),
        focusedLabelColor = Color(0xFF2563EB),
        cursorColor = Color(0xFF2563EB)
    )
