package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.api.toBase64
import com.example.data.model.MessageEntity
import com.example.ui.MainViewModel
import com.example.ui.components.MarkdownText
import com.example.ui.components.NavDestination

@Composable
fun ChatScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.currentMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val activeConvoId by viewModel.activeConversationId.collectAsState()
    val activeConvo = remember(activeConvoId, conversations) {
        conversations.find { it.id == activeConvoId }
    }

    var inputText by remember { mutableStateOf("") }
    var selectedImageBase64 by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var newTitleInput by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    selectedImageBase64 = bitmap.toBase64()
                    Toast.makeText(context, "Image attached for Gemini multimodal analysis", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Auto-scroll on new message
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF00E5FF), Color(0xFFA855F7))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.soanex_logo),
                        contentDescription = "SOANEX AI",
                        modifier = Modifier.size(28.dp).clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = activeConvo?.title ?: "SOANEX AI Chat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "Google Gemini 3.5 • Neural Context Active",
                        fontSize = 11.sp,
                        color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.createNewChat() }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "New chat",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rename Chat") },
                            onClick = {
                                showMenu = false
                                newTitleInput = activeConvo?.title ?: ""
                                showRenameDialog = true
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear / Delete Chat") },
                            onClick = {
                                showMenu = false
                                activeConvoId?.let { viewModel.deleteConversation(it) }
                            },
                            leadingIcon = { Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red) }
                        )
                    }
                }
            }
        }

        // Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // If empty, show hero welcome card
            if (messages.isEmpty()) {
                item {
                    ChatWelcomeCard(onSelectPrompt = { prompt ->
                        viewModel.sendMessage(prompt)
                    })
                }
            } else {
                items(messages, key = { it.id }) { message ->
                    ChatMessageItem(
                        message = message,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("SOANEX AI", message.content))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onRegenerate = {
                            viewModel.sendMessage("Please regenerate and improve the previous response.")
                        }
                    )
                }
            }

            if (isGenerating) {
                item {
                    GeneratingIndicator()
                }
            }
        }

        // Image Attachment Preview if any
        AnimatedVisibility(
            visible = selectedImageUri != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Attached Image",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Image Attached",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF)
                    )
                    Text(
                        "Gemini Multimodal image understanding active",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = {
                    selectedImageUri = null
                    selectedImageBase64 = null
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Remove image", tint = Color(0xFF94A3B8))
                }
            }
        }

        // Quick Suggestions Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickChip("🌱 Photosynthesis") { viewModel.sendMessage("Explain photosynthesis.") }
            QuickChip("✨ Make it easier") { viewModel.sendMessage("Make it easier and simpler to understand.") }
            QuickChip("💻 Python code") { viewModel.sendMessage("Write a clean Python script for data processing.") }
            QuickChip("🧠 What is my name?") { viewModel.sendMessage("What is my name?") }
        }

        // Input Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attach image
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.testTag("chat_attach_button")
                ) {
                    Icon(
                        Icons.Default.AttachFile,
                        contentDescription = "Attach image",
                        tint = Color(0xFF38BDF8)
                    )
                }

                // Text field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            "Message SOANEX AI...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    maxLines = 4,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank() || selectedImageBase64 != null) {
                                viewModel.sendMessage(inputText, selectedImageBase64)
                                inputText = ""
                                selectedImageUri = null
                                selectedImageBase64 = null
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Microphone action -> Voice Assistant
                IconButton(
                    onClick = {
                        viewModel.navigateTo(NavDestination.VOICE)
                    },
                    modifier = Modifier.testTag("chat_mic_button")
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Voice Assistant",
                        tint = Color(0xFFA855F7)
                    )
                }

                // Send button
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() || selectedImageBase64 != null) {
                            viewModel.sendMessage(inputText, selectedImageBase64)
                            inputText = ""
                            selectedImageUri = null
                            selectedImageBase64 = null
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF00E5FF), Color(0xFF6366F1))
                            )
                        )
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send message",
                        tint = Color(0xFF042F2E),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Rename Dialog
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Conversation") },
            text = {
                OutlinedTextField(
                    value = newTitleInput,
                    onValueChange = { newTitleInput = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTitleInput.isNotBlank()) {
                            activeConvoId?.let { viewModel.renameConversation(it, newTitleInput) }
                            showRenameDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatWelcomeCard(onSelectPrompt: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Image
        Image(
            painter = painterResource(id = R.drawable.soanex_hero_banner),
            contentDescription = "SOANEX AI Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "SOANEX AI",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "“Your AI, Your Way.”",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFA855F7)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Personalized AI intelligence with Multimodal vision, Memory, Image Generation, Voice, and Study assistance.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PromptSuggestionButton(
                title = "Explain photosynthesis",
                subtitle = "Try context follow-up: 'Make it easier'",
                icon = "🌱"
            ) { onSelectPrompt("Explain photosynthesis.") }

            PromptSuggestionButton(
                title = "Debug Kotlin coroutines",
                subtitle = "Coding assistant with syntax highlighted snippets",
                icon = "⚡"
            ) { onSelectPrompt("Help me debug and optimize a Kotlin coroutine flow.") }

            PromptSuggestionButton(
                title = "My name is Soaib",
                subtitle = "Save directly to Personal Memory for future context",
                icon = "🧠"
            ) { onSelectPrompt("My name is Soaib.") }
        }
    }
}

@Composable
fun PromptSuggestionButton(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: MessageEntity,
    onCopy: () -> Unit,
    onRegenerate: () -> Unit
) {
    val isUser = message.role.equals("USER", ignoreCase = true)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF00E5FF), Color(0xFFA855F7))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.soanex_logo),
                    contentDescription = "SOANEX AI",
                    modifier = Modifier.size(24.dp).clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(
            modifier = Modifier.weight(1f, fill = false),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Optional image if attached
            if (!message.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = message.imageUri,
                    contentDescription = "Attached image",
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isUser) Color(0xFF0C4A6E) else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        1.dp,
                        if (isUser) Color(0xFF00E5FF).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .padding(14.dp)
            ) {
                MarkdownText(
                    markdown = message.content,
                    textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            // Message actions for Assistant
            if (!isUser) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.isDemo) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF59E0B).copy(alpha = 0.18f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Demo Simulation Mode", fontSize = 10.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(26.dp)) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy message",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(onClick = onRegenerate, modifier = Modifier.size(26.dp)) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Regenerate response",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GeneratingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = Color(0xFF00E5FF)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "SOANEX AI is thinking...",
            fontSize = 12.sp,
            color = Color(0xFF00E5FF),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun QuickChip(text: String, onClick: () -> Unit) {
    ElevatedAssistChip(
        onClick = onClick,
        label = { Text(text, fontSize = 11.sp) }
    )
}
