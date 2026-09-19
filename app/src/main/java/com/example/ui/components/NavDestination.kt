package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavDestination(
    val title: String,
    val icon: ImageVector,
    val category: String = "Main"
) {
    CHATS("Chats", Icons.Default.History, "Conversations"),
    AI_ASSISTANT("AI Assistant", Icons.Default.Chat, "Conversations"),
    IMAGE_GENERATOR("Image Generator", Icons.Default.Image, "Creation"),
    VOICE("Voice", Icons.Default.Mic, "Creation"),
    DOCUMENTS("Documents", Icons.Default.Description, "Productivity"),
    WEB_SEARCH("Web Search", Icons.Default.Search, "Research"),
    STUDY("Study", Icons.Default.School, "Productivity"),
    CODING("Coding", Icons.Default.Code, "Development"),
    WRITING("Writing", Icons.Default.EditNote, "Creation"),
    TASKS("Tasks", Icons.Default.TaskAlt, "Productivity"),
    MEMORY("Memory", Icons.Default.Psychology, "Personalization"),
    SETTINGS("Settings", Icons.Default.Settings, "System"),

    // Bottom items
    PROFILE("Profile", Icons.Default.Person, "Account"),
    SUBSCRIPTION("Subscription", Icons.Default.Star, "Account"),
    HELP("Help & Shortcuts", Icons.AutoMirrored.Filled.Help, "Account")
}
