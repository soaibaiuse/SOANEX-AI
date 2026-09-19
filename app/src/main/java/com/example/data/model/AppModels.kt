package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserPlan(val title: String, val price: String, val dailyTokenLimit: Int) {
    FREE("Free Plan", "₹0/month", 20),
    PRO("Pro Plan", "₹99/month", 500)
}

data class UserProfile(
    val id: String = "user_soanex_1",
    val name: String = "Soaib Ali",
    val email: String = "soaibali98010@gmail.com",
    val avatarUrl: String = "",
    val createdAt: String = "September 2026",
    val plan: UserPlan = UserPlan.PRO,
    val queriesUsedToday: Int = 12,
    val imagesGeneratedToday: Int = 3
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val category: String = "General"
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val role: String, // "USER" or "ASSISTANT"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val isError: Boolean = false,
    val isDemo: Boolean = false
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val key: String,
    val value: String,
    val category: String = "Personal",
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val priority: String = "MEDIUM", // "HIGH", "MEDIUM", "LOW"
    val dueDate: String = "Tomorrow, 5:00 PM",
    val isCompleted: Boolean = false,
    val reminderSet: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val fileType: String = "PDF", // "PDF", "DOC", "TXT"
    val fileSize: String = "1.2 MB",
    val contentSnippet: String = "",
    val summary: String = "",
    val keyPoints: String = "", // JSON or bullet lines
    val uploadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "generated_images")
data class GeneratedImageEntity(
    @PrimaryKey val id: String,
    val prompt: String,
    val style: String = "Cinematic",
    val aspectRatio: String = "1:1",
    val imageUrl: String = "",
    val isSimulated: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiPreferences(
    val assistantName: String = "SOANEX",
    val preferredLanguage: String = "English", // English, Hindi, Spanish, French, etc.
    val tone: String = "Friendly", // Friendly, Professional, Simple, Formal, Creative, Concise, Detailed
    val responseStyle: String = "Detailed", // Detailed, Concise, Bullet Points, Academic
    val memoryEnabled: Boolean = true,
    val customInstructions: String = "Always provide structured, clear, and modern insights with code blocks and bullet points where applicable."
)

enum class AppThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}
