package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.ui.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TasksScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tasks by viewModel.tasks.collectAsState()
    var activeFilter by remember { mutableStateOf(0) }
    val filters = listOf("All", "Pending", "Completed")

    var showAddDialog by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    var taskPriority by remember { mutableStateOf("MEDIUM") }
    var taskDueDate by remember { mutableStateOf("Tomorrow, 5:00 PM") }

    val filteredTasks = remember(tasks, activeFilter) {
        when (activeFilter) {
            1 -> tasks.filter { !it.isCompleted }
            2 -> tasks.filter { it.isCompleted }
            else -> tasks
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tasks & Reminders",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "AI-assisted task tracking with priority and due dates",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Tabs
            TabRow(
                selectedTabIndex = activeFilter,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color(0xFF00E5FF)
            ) {
                filters.forEachIndexed { index, title ->
                    Tab(
                        selected = activeFilter == index,
                        onClick = { activeFilter = index },
                        text = { Text(title, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No tasks found", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            onToggle = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.taskRepo.toggleTaskCompletion(task)
                                }
                            },
                            onDelete = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.taskRepo.deleteTask(task.id)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = Color(0xFF00E5FF),
            contentColor = Color(0xFF042F2E),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_task_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add task")
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("New Task / Reminder") },
            text = {
                Column {
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Task Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = taskDesc,
                        onValueChange = { taskDesc = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = taskDueDate,
                        onValueChange = { taskDueDate = it },
                        label = { Text("Due Date & Time") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Priority", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                            FilterChip(
                                selected = taskPriority == p,
                                onClick = { taskPriority = p },
                                label = { Text(p, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.taskRepo.addTask(taskTitle, taskDesc, taskPriority, taskDueDate)
                            }
                            taskTitle = ""
                            taskDesc = ""
                            showAddDialog = false
                            Toast.makeText(context, "Task created", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Create Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TaskItemCard(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle completion",
                    tint = if (task.isCompleted) Color(0xFF10B981) else Color(0xFF38BDF8)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Alarm, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(task.dueDate, fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (task.priority) {
                                    "HIGH" -> Color(0xFFF43F5E).copy(alpha = 0.2f)
                                    "MEDIUM" -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                    else -> Color(0xFF10B981).copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            task.priority,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (task.priority) {
                                "HIGH" -> Color(0xFFF43F5E)
                                "MEDIUM" -> Color(0xFFF59E0B)
                                else -> Color(0xFF10B981)
                            }
                        )
                    }
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
            }
        }
    }
}
