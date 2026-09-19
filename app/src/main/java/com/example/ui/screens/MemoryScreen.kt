package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemoryEntity
import com.example.ui.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MemoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val memories by viewModel.memories.collectAsState()
    val aiPrefs by viewModel.aiPreferences.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingMemory by remember { mutableStateOf<MemoryEntity?>(null) }
    var keyInput by remember { mutableStateOf("") }
    var valueInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("Personal") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Personal Memory",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "“Your AI, Your Way.” • User-Controlled Memory Engine",
                        fontSize = 12.sp,
                        color = Color(0xFFA855F7),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Button(
                onClick = {
                    keyInput = ""
                    valueInput = ""
                    categoryInput = "Personal"
                    showAddDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF042F2E)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_memory_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Fact", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Master Memory Toggle Card
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
                Column(modifier = Modifier.weight(1f)) {
                    Text("Enable Personal Memory", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        "When active, SOANEX injects verified user facts into the system prompt for personalized responses.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
                Switch(
                    checked = aiPrefs.memoryEnabled,
                    onCheckedChange = { isEnabled ->
                        viewModel.updateAiPreferences(aiPrefs.copy(memoryEnabled = isEnabled))
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF0C4A6E))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Saved Memory Items (${memories.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)

            if (memories.isNotEmpty()) {
                TextButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.memoryRepo.clearAll()
                        }
                        Toast.makeText(context, "All memory cleared", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All", color = Color.Red.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (memories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No personal memories saved yet", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Text("Say 'My name is...' in chat or add custom facts here.", color = Color(0xFF64748B), fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(memories, key = { it.id }) { memory ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(memory.category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(memory.key, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(memory.value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            // Active toggle
                            Switch(
                                checked = memory.isEnabled,
                                onCheckedChange = { checked ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.memoryRepo.updateMemory(memory.copy(isEnabled = checked))
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = {
                                    editingMemory = memory
                                    keyInput = memory.key
                                    valueInput = memory.value
                                    categoryInput = memory.category
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.memoryRepo.deleteMemory(memory.id)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Memory Dialog
    if (showAddDialog || editingMemory != null) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingMemory = null
            },
            title = { Text(if (editingMemory != null) "Edit Memory Item" else "Add New Fact") },
            text = {
                Column {
                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        label = { Text("Memory Key (e.g. Name, Tech Stack, Goal)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = valueInput,
                        onValueChange = { valueInput = it },
                        label = { Text("Value (e.g. Soaib Ali, Kotlin + Compose)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        label = { Text("Category (Personal, Work, Preferences)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (keyInput.isNotBlank() && valueInput.isNotBlank()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                if (editingMemory != null) {
                                    viewModel.memoryRepo.updateMemory(
                                        editingMemory!!.copy(key = keyInput, value = valueInput, category = categoryInput)
                                    )
                                } else {
                                    viewModel.memoryRepo.saveMemory(keyInput, valueInput, categoryInput)
                                }
                            }
                            showAddDialog = false
                            editingMemory = null
                            Toast.makeText(context, "Memory updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save Memory")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    editingMemory = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
