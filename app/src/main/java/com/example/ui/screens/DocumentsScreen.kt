package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentEntity
import com.example.ui.MainViewModel
import com.example.ui.components.MarkdownText
import com.example.ui.components.NavDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DocumentsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val documents by viewModel.documents.collectAsState()
    var selectedDoc by remember { mutableStateOf<DocumentEntity?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }

    var docTitle by remember { mutableStateOf("project_proposal.pdf") }
    var docContent by remember {
        mutableStateOf(
            "Project Title: SOANEX AI SaaS Architecture\n" +
                    "Objective: Scalable production-ready AI platform deployable to Netlify with multi-provider abstraction.\n" +
                    "Core Deliverables:\n" +
                    "1. Real-time Gemini 3.5 AI integration with streaming responses.\n" +
                    "2. Personal Memory engine for user preferences and custom identity.\n" +
                    "3. Multimodal vision and document Q&A parsing.\n" +
                    "4. Pro subscription tier at ₹99/month with automated billing checks.\n" +
                    "5. Offline-first demo fallback mode for high availability."
        )
    }

    LaunchedEffect(documents) {
        if (selectedDoc == null && documents.isNotEmpty()) {
            selectedDoc = documents.first()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Document & PDF Assistant",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Upload, summarize, extract key points & ask questions",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showUploadDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF042F2E)),
                modifier = Modifier.testTag("doc_upload_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Upload Document", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (documents.isEmpty()) {
            // Seed prompt
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No documents uploaded yet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Upload a PDF or document snippet to summarize and chat with it.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.documentRepo.addDocument(docTitle, "PDF", docContent)
                            }
                        }
                    ) {
                        Text("Add Sample 'project_proposal.pdf'")
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Document List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(documents, key = { it.id }) { doc ->
                        val isSelected = selectedDoc?.id == doc.id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF0C4A6E) else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDoc = doc }
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (doc.fileType == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (doc.fileType == "PDF") Color(0xFFF43F5E) else Color(0xFF38BDF8),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = doc.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${doc.fileType} • ${doc.fileSize}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            viewModel.documentRepo.deleteDocument(doc.id)
                                            if (selectedDoc?.id == doc.id) {
                                                selectedDoc = null
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Right Column: Document Details & Q&A
                selectedDoc?.let { doc ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .weight(1.4f)
                            .fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Analyzed with Gemini Multimodal Engine", fontSize = 11.sp, color = Color(0xFF00E5FF))
                                }
                                Button(
                                    onClick = {
                                        viewModel.createNewChat()
                                        viewModel.sendMessage("I'm analyzing document '${doc.title}'. Here is the content:\n${doc.contentSnippet}\n\nWhat are the primary takeaways and next steps?")
                                        viewModel.navigateTo(NavDestination.AI_ASSISTANT)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ask AI", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Summary", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF38BDF8))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(doc.summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Key Points & Deliverables", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFA855F7))
                            Spacer(modifier = Modifier.height(4.dp))
                            MarkdownText(markdown = doc.keyPoints)

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Document Content Snippet", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(10.dp)
                            ) {
                                Text(doc.contentSnippet, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Upload dialog
    if (showUploadDialog) {
        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            title = { Text("Upload or Paste Document") },
            text = {
                Column {
                    OutlinedTextField(
                        value = docTitle,
                        onValueChange = { docTitle = it },
                        label = { Text("Document Title (e.g. thesis.pdf)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = docContent,
                        onValueChange = { docContent = it },
                        label = { Text("Content or Paste Text") },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (docTitle.isNotBlank()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val created = viewModel.documentRepo.addDocument(docTitle, if (docTitle.endsWith(".pdf", true)) "PDF" else "DOC", docContent)
                                selectedDoc = created
                            }
                            showUploadDialog = false
                            Toast.makeText(context, "Document uploaded and indexed successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Upload & Analyze")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
