package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.MarkdownText

enum class WritingType(val label: String) {
    EMAIL("Email"),
    ESSAY("Essay"),
    BLOG("Blog Post"),
    SOCIAL("Social Media"),
    ASSIGNMENT("Assignment"),
    GRAMMAR("Grammar Check")
}

@Composable
fun WritingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(WritingType.EMAIL) }
    var selectedTone by remember { mutableStateOf("Professional") }
    val tones = listOf("Professional", "Friendly", "Formal", "Simple", "Creative", "Concise", "Detailed")

    var promptInput by remember {
        mutableStateOf("Write a professional email asking for an update on project approval.")
    }

    var generatedDraft by remember {
        mutableStateOf(
            "Subject: Follow-up regarding Project Approval & Next Milestones\n\n" +
                    "Dear [Manager/Colleague],\n\n" +
                    "I hope this message finds you well.\n\n" +
                    "I am writing to respectfully request an update regarding the approval status for the **SOANEX AI Initiative** submitted earlier this week. Our engineering team is prepared to transition immediately into phase two upon your confirmation.\n\n" +
                    "Please let me know if you need any additional documentation, architectural breakdowns, or metric projections.\n\n" +
                    "Thank you for your time and guidance.\n\n" +
                    "Best regards,\n" +
                    "**Soaib Ali**\n" +
                    "Lead Developer"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.EditNote, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Writing Assistant",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Emails, essays, blogs & tone-adaptive copywriting",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Types
        Text("Writing Format", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WritingType.values().forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text(type.label, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tone Selector
        Text("Tone of Voice", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tones.forEach { tone ->
                FilterChip(
                    selected = selectedTone == tone,
                    onClick = { selectedTone = tone },
                    label = { Text(tone, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Prompt input
        OutlinedTextField(
            value = promptInput,
            onValueChange = { promptInput = it },
            label = { Text("What should SOANEX write?") },
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("writing_prompt_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                generatedDraft = "Draft for: \"$promptInput\"\nTone: $selectedTone • Format: ${selectedType.label}\n\n" +
                        "Here is your customized, polished draft:\n\n" +
                        "Dear Partner,\n\n" +
                        "Regarding our recent discussion, we are eager to proceed with $promptInput. Our team has tuned all deliverables to match our target specifications.\n\n" +
                        "Warm regards,\nSoaib Ali"
                Toast.makeText(context, "Draft synthesized", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("writing_generate_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF042F2E)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Generate Content", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Result Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Generated Draft", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF00E5FF))
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Draft", generatedDraft))
                            Toast.makeText(context, "Draft copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                MarkdownText(markdown = generatedDraft)
            }
        }
    }
}
