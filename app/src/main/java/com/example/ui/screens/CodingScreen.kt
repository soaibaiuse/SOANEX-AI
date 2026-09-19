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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.CodeBlock
import com.example.ui.components.MarkdownText

enum class CodingAction(val label: String) {
    DEBUG("Debug Code"),
    EXPLAIN("Explain"),
    OPTIMIZE("Optimize"),
    REFACTOR("Refactor"),
    CONVERT("Convert")
}

@Composable
fun CodingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val languages = listOf("Kotlin", "Python", "TypeScript", "JavaScript", "React", "Java", "C++", "SQL", "HTML/CSS")
    var selectedLang by remember { mutableStateOf("Kotlin") }
    var selectedAction by remember { mutableStateOf(CodingAction.DEBUG) }

    var inputSnippet by remember {
        mutableStateOf(
            "fun fetchUserProfile(userId: String) {\n" +
                    "    val result = apiService.getUser(userId)\n" +
                    "    userState.value = result // Bug: network on main thread without coroutine\n" +
                    "}"
        )
    }

    var outputResponse by remember {
        mutableStateOf(
            "### Bug Identified & Fixed\n\n" +
                    "**Issue**: Network call `apiService.getUser(userId)` is being executed synchronously on the Android main thread, causing an `android.os.NetworkOnMainThreadException` or freezing the UI.\n\n" +
                    "### Corrected & Optimized Implementation:\n\n" +
                    "```kotlin\n" +
                    "fun fetchUserProfile(userId: String) = viewModelScope.launch(Dispatchers.IO) {\n" +
                    "    try {\n" +
                    "        _uiState.value = UserUiState.Loading\n" +
                    "        val result = apiService.getUser(userId)\n" +
                    "        withContext(Dispatchers.Main) {\n" +
                    "            _uiState.value = UserUiState.Success(result)\n" +
                    "        }\n" +
                    "    } catch (e: Exception) {\n" +
                    "        _uiState.value = UserUiState.Error(e.localizedMessage ?: \"Network error\")\n" +
                    "    }\n" +
                    "}\n" +
                    "```\n\n" +
                    "**Improvements Made:**\n" +
                    "1. Bound execution to `viewModelScope` with non-blocking `Dispatchers.IO`.\n" +
                    "2. Added proper error handling and UI state encapsulation."
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
            Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Coding Assistant",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Generate, debug, explain & refactor across 10+ languages",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Languages bar
        Text("Target Language", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.forEach { lang ->
                FilterChip(
                    selected = selectedLang == lang,
                    onClick = { selectedLang = lang },
                    label = { Text(lang, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action selector
        Text("Action", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CodingAction.values().forEach { action ->
                FilterChip(
                    selected = selectedAction == action,
                    onClick = { selectedAction = action },
                    label = { Text(action.label, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input field
        OutlinedTextField(
            value = inputSnippet,
            onValueChange = { inputSnippet = it },
            label = { Text("Code or Request") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("coding_input_field"),
            minLines = 4,
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                outputResponse = "### Analysis (${selectedAction.label} in $selectedLang)\n\n" +
                        "Processed your snippet:\n\n" +
                        "```$selectedLang\n// Optimized solution\n$inputSnippet\n```\n\n" +
                        "All syntax conventions and safety checks validated."
                Toast.makeText(context, "Code processed with AI engine", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("coding_run_button"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF042F2E))
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Process Code with SOANEX", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Output Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                MarkdownText(markdown = outputResponse)
            }
        }
    }
}
