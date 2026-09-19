package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val aiPrefs by viewModel.aiPreferences.collectAsState()

    var assistantName by remember(aiPrefs) { mutableStateOf(aiPrefs.assistantName) }
    var preferredLanguage by remember(aiPrefs) { mutableStateOf(aiPrefs.preferredLanguage) }
    var selectedTone by remember(aiPrefs) { mutableStateOf(aiPrefs.tone) }
    var selectedStyle by remember(aiPrefs) { mutableStateOf(aiPrefs.responseStyle) }
    var customInstructions by remember(aiPrefs) { mutableStateOf(aiPrefs.customInstructions) }

    val languages = listOf("English", "Hindi", "Spanish", "French", "German", "Japanese")
    val tones = listOf("Friendly", "Professional", "Simple", "Formal", "Creative", "Concise", "Detailed")
    val styles = listOf("Detailed", "Concise", "Bullet Points", "Academic")

    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Configure appearance, AI personality & privacy controls",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Appearance Section
        Text("Appearance", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Brightness4, contentDescription = null, tint = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Dark Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(if (isDarkMode) "Obsidian dark theme active" else "Clean slate light theme active", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode() },
                    modifier = Modifier.testTag("theme_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AI Preferences Section (“Your AI, Your Way.”)
        Text("AI Preferences (“Your AI, Your Way”)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF00E5FF))
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Assistant Name
                OutlinedTextField(
                    value = assistantName,
                    onValueChange = { assistantName = it },
                    label = { Text("Assistant Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Preferred Language
                Text("Preferred Language", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    languages.forEach { lang ->
                        FilterChip(
                            selected = preferredLanguage == lang,
                            onClick = { preferredLanguage = lang },
                            label = { Text(lang, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tone
                Text("Tone of Voice", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tones.forEach { t ->
                        FilterChip(
                            selected = selectedTone == t,
                            onClick = { selectedTone = t },
                            label = { Text(t, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Response Style
                Text("Response Style", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    styles.forEach { s ->
                        FilterChip(
                            selected = selectedStyle == s,
                            onClick = { selectedStyle = s },
                            label = { Text(s, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Instructions
                OutlinedTextField(
                    value = customInstructions,
                    onValueChange = { customInstructions = it },
                    label = { Text("Custom System Instructions") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.updateAiPreferences(
                            aiPrefs.copy(
                                assistantName = assistantName,
                                preferredLanguage = preferredLanguage,
                                tone = selectedTone,
                                responseStyle = selectedStyle,
                                customInstructions = customInstructions
                            )
                        )
                        Toast.makeText(context, "AI preferences saved", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF042F2E)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save AI Preferences", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy & Data
        Text("Privacy & Data", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Exported user data and conversations to soanex_backup.json", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export All Data (JSON)")
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showClearHistoryDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear All Chat Conversations", color = Color.Red)
                }
            }
        }
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear All Conversations?") },
            text = { Text("This will permanently remove all previous chat history from local Room storage.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createNewChat()
                        showClearHistoryDialog = false
                        Toast.makeText(context, "Chat history cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
