package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.api.AiProvider
import com.example.data.api.AiResponse
import com.example.data.api.GeminiProvider
import com.example.data.api.ProviderType
import com.example.data.local.AppDatabase
import com.example.data.model.AiPreferences
import com.example.data.model.AppThemeMode
import com.example.data.model.ConversationEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.GeneratedImageEntity
import com.example.data.model.MemoryEntity
import com.example.data.model.MessageEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserProfile
import com.example.data.model.UserPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

class AuthRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("soanex_auth", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        val savedEmail = prefs.getString("saved_email", null)
        val savedName = prefs.getString("saved_name", "Soaib Ali")
        if (savedEmail != null) {
            _currentUser.value = UserProfile(
                id = "user_soanex_1",
                name = savedName ?: "Soaib Ali",
                email = savedEmail,
                plan = UserPlan.PRO
            )
            _isAuthenticated.value = true
        } else {
            // Default logged in user for instant preview experience
            _currentUser.value = UserProfile()
            _isAuthenticated.value = true
        }
    }

    fun login(email: String, password: String): Boolean {
        if (email.isNotBlank() && password.length >= 6) {
            val name = if (email.contains("@")) email.substringBefore("@").replace(".", " ").capitalize() else "Soaib Ali"
            val user = UserProfile(name = name, email = email, plan = UserPlan.PRO)
            _currentUser.value = user
            _isAuthenticated.value = true
            prefs.edit().putString("saved_email", email).putString("saved_name", name).apply()
            return true
        }
        return false
    }

    fun signup(name: String, email: String, password: String): Boolean {
        if (name.isNotBlank() && email.isNotBlank() && password.length >= 6) {
            val user = UserProfile(name = name, email = email, plan = UserPlan.PRO)
            _currentUser.value = user
            _isAuthenticated.value = true
            prefs.edit().putString("saved_email", email).putString("saved_name", name).apply()
            return true
        }
        return false
    }

    fun logout() {
        prefs.edit().clear().apply()
        _currentUser.value = null
        _isAuthenticated.value = false
    }

    fun updateProfile(name: String, email: String) {
        _currentUser.value?.let { current ->
            val updated = current.copy(name = name, email = email)
            _currentUser.value = updated
            prefs.edit().putString("saved_email", email).putString("saved_name", name).apply()
        }
    }

    fun upgradeToPro() {
        _currentUser.value?.let { current ->
            _currentUser.value = current.copy(plan = UserPlan.PRO)
        }
    }
}

class ChatRepository(
    private val db: AppDatabase,
    private val aiProvider: AiProvider = GeminiProvider()
) {
    val conversations: Flow<List<ConversationEntity>> = db.conversationDao().getAllConversations()

    suspend fun getOrCreateInitialConversation(): String = withContext(Dispatchers.IO) {
        val existing = db.conversationDao().getAllConversations().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val welcomeId = UUID.randomUUID().toString()
            val welcomeConvo = ConversationEntity(
                id = welcomeId,
                title = "Welcome to SOANEX AI",
                category = "General"
            )
            db.conversationDao().insertConversation(welcomeConvo)

            val welcomeMsg = MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = welcomeId,
                role = "ASSISTANT",
                content = "# Welcome to SOANEX AI\n### “Your AI, Your Way.”\n\nI am your personalized AI assistant, powered by Google AI Studio and Gemini models.\n\nHere are some things we can do together:\n\n- 💬 **Intelligent Chat**: Ask complex questions, brainstorm ideas, or debate concepts.\n- 🧠 **Personal Memory**: I can remember your name, goals, and style preferences.\n- 🎨 **Image Generation**: Create futuristic digital art and photorealistic visuals.\n- 🎙️ **Voice Assistant**: Natural voice conversation with real-time speech synthesis.\n- 💻 **Coding**: Generate, debug, explain, and refactor code in 10+ programming languages.\n- 📚 **Study & Research**: Summarize chapters, generate practice MCQs, and prepare for exams.\n- 📄 **Document Analysis**: Upload PDFs and documents for instant extraction and Q&A.\n\n*How would you like to begin today?*"
            )
            db.messageDao().insertMessage(welcomeMsg)
            welcomeId
        } else {
            existing.first().id
        }
    }

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> {
        return db.messageDao().getMessagesForConversation(conversationId)
    }

    suspend fun createNewConversation(title: String = "New Chat", category: String = "General"): String = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val convo = ConversationEntity(
            id = id,
            title = title,
            category = category
        )
        db.conversationDao().insertConversation(convo)
        id
    }

    suspend fun renameConversation(id: String, newTitle: String) = withContext(Dispatchers.IO) {
        val existing = db.conversationDao().getConversationById(id)
        if (existing != null) {
            db.conversationDao().updateConversation(existing.copy(title = newTitle, updatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun deleteConversation(id: String) = withContext(Dispatchers.IO) {
        db.messageDao().deleteMessagesForConversation(id)
        db.conversationDao().deleteConversation(id)
    }

    suspend fun sendMessage(
        conversationId: String,
        content: String,
        imageBase64: String? = null,
        preferences: AiPreferences,
        memories: List<MemoryEntity>
    ): MessageEntity = withContext(Dispatchers.IO) {
        // Insert user message
        val userMsg = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = "USER",
            content = content,
            imageUri = if (imageBase64 != null) "data:image/jpeg;base64,$imageBase64" else null
        )
        db.messageDao().insertMessage(userMsg)

        // Check if user is asking to save a memory e.g. "My name is Soaib" or "Remember that ..."
        checkAndSaveAutomaticMemory(content, db)

        // Update conversation timestamp and title if it's default
        val convo = db.conversationDao().getConversationById(conversationId)
        if (convo != null) {
            val newTitle = if (convo.title == "New Chat") {
                if (content.length > 28) content.take(28) + "..." else content
            } else convo.title
            db.conversationDao().updateConversation(convo.copy(title = newTitle, updatedAt = System.currentTimeMillis()))
        }

        // Fetch recent message history for context
        val historyList = db.messageDao().getMessagesForConversation(conversationId).firstOrNull() ?: emptyList()
        val historyPairs = historyList.takeLast(6).map { it.role.lowercase() to it.content }

        // Call AI Provider
        val response = aiProvider.generateResponse(
            prompt = content,
            history = historyPairs,
            preferences = preferences,
            memories = memories,
            imageBase64 = imageBase64
        )

        // Insert assistant response
        val assistantMsg = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = "ASSISTANT",
            content = response.text,
            isDemo = response.isDemo
        )
        db.messageDao().insertMessage(assistantMsg)
        assistantMsg
    }

    private suspend fun checkAndSaveAutomaticMemory(content: String, db: AppDatabase) {
        val lower = content.lowercase()
        if (lower.startsWith("my name is ")) {
            val name = content.substring(11).trim().removeSuffix(".")
            val mem = MemoryEntity(
                id = UUID.randomUUID().toString(),
                key = "Name",
                value = name,
                category = "Identity"
            )
            db.memoryDao().insertMemory(mem)
        } else if (lower.startsWith("remember that ") || lower.startsWith("remember: ")) {
            val fact = content.substringAfter("remember").replace("that", "").replace(":", "").trim()
            if (fact.isNotBlank()) {
                val mem = MemoryEntity(
                    id = UUID.randomUUID().toString(),
                    key = "Fact",
                    value = fact,
                    category = "Preference"
                )
                db.memoryDao().insertMemory(mem)
            }
        }
    }
}

class MemoryRepository(private val db: AppDatabase) {
    val memories: Flow<List<MemoryEntity>> = db.memoryDao().getAllMemories()

    suspend fun getEnabledMemories(): List<MemoryEntity> = withContext(Dispatchers.IO) {
        db.memoryDao().getEnabledMemories()
    }

    suspend fun saveMemory(key: String, value: String, category: String = "Personal") = withContext(Dispatchers.IO) {
        val entity = MemoryEntity(
            id = UUID.randomUUID().toString(),
            key = key.trim(),
            value = value.trim(),
            category = category
        )
        db.memoryDao().insertMemory(entity)
    }

    suspend fun updateMemory(memory: MemoryEntity) = withContext(Dispatchers.IO) {
        db.memoryDao().updateMemory(memory)
    }

    suspend fun deleteMemory(id: String) = withContext(Dispatchers.IO) {
        db.memoryDao().deleteMemory(id)
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        db.memoryDao().clearAllMemories()
    }
}

class TaskRepository(private val db: AppDatabase) {
    val tasks: Flow<List<TaskEntity>> = db.taskDao().getAllTasks()

    suspend fun addTask(
        title: String,
        description: String = "",
        priority: String = "MEDIUM",
        dueDate: String = "Tomorrow, 5:00 PM"
    ) = withContext(Dispatchers.IO) {
        val task = TaskEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            priority = priority,
            dueDate = dueDate
        )
        db.taskDao().insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        db.taskDao().updateTask(task)
    }

    suspend fun toggleTaskCompletion(task: TaskEntity) = withContext(Dispatchers.IO) {
        db.taskDao().updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    suspend fun deleteTask(id: String) = withContext(Dispatchers.IO) {
        db.taskDao().deleteTask(id)
    }
}

class DocumentRepository(private val db: AppDatabase) {
    val documents: Flow<List<DocumentEntity>> = db.documentDao().getAllDocuments()

    suspend fun addDocument(
        title: String,
        fileType: String = "PDF",
        contentSnippet: String
    ): DocumentEntity = withContext(Dispatchers.IO) {
        val summary = "A structured analysis of $title covering primary findings, methodology, and strategic recommendations."
        val keyPoints = "• Primary theme focused on $title\n• Contains comprehensive structural insights\n• Ready for interactive AI synthesis and questions"
        val doc = DocumentEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            fileType = fileType,
            contentSnippet = contentSnippet,
            summary = summary,
            keyPoints = keyPoints
        )
        db.documentDao().insertDocument(doc)
        doc
    }

    suspend fun deleteDocument(id: String) = withContext(Dispatchers.IO) {
        db.documentDao().deleteDocument(id)
    }
}

class ImageGenRepository(private val db: AppDatabase) {
    val images: Flow<List<GeneratedImageEntity>> = db.imageDao().getAllImages()

    suspend fun generateImage(
        prompt: String,
        style: String = "Cinematic",
        aspectRatio: String = "1:1"
    ): GeneratedImageEntity = withContext(Dispatchers.IO) {
        // High quality simulated AI image generation with custom art placeholder
        val entity = GeneratedImageEntity(
            id = UUID.randomUUID().toString(),
            prompt = prompt,
            style = style,
            aspectRatio = aspectRatio,
            imageUrl = "",
            isSimulated = true
        )
        db.imageDao().insertImage(entity)
        entity
    }

    suspend fun deleteImage(id: String) = withContext(Dispatchers.IO) {
        db.imageDao().deleteImage(id)
    }
}
