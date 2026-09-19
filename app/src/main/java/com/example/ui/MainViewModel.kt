package com.example.ui

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiProvider
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
import com.example.data.repository.AuthRepository
import com.example.data.repository.ChatRepository
import com.example.data.repository.DocumentRepository
import com.example.data.repository.ImageGenRepository
import com.example.data.repository.MemoryRepository
import com.example.data.repository.TaskRepository
import com.example.ui.components.NavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val authRepo = AuthRepository(application)
    val chatRepo = ChatRepository(db, GeminiProvider())
    val memoryRepo = MemoryRepository(db)
    val taskRepo = TaskRepository(db)
    val documentRepo = DocumentRepository(db)
    val imageRepo = ImageGenRepository(db)

    // Navigation & UI States
    private val _currentDestination = MutableStateFlow(NavDestination.AI_ASSISTANT)
    val currentDestination: StateFlow<NavDestination> = _currentDestination.asStateFlow()

    private val _activeConversationId = MutableStateFlow<String?>(null)
    val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _aiPreferences = MutableStateFlow(AiPreferences())
    val aiPreferences: StateFlow<AiPreferences> = _aiPreferences.asStateFlow()

    // Database flows
    val conversations: StateFlow<List<ConversationEntity>> = chatRepo.conversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentMessages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val currentMessages: StateFlow<List<MessageEntity>> = _currentMessages.asStateFlow()

    val memories: StateFlow<List<MemoryEntity>> = memoryRepo.memories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = taskRepo.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val documents: StateFlow<List<DocumentEntity>> = documentRepo.documents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val generatedImages: StateFlow<List<GeneratedImageEntity>> = imageRepo.images
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUser: StateFlow<UserProfile?> = authRepo.currentUser
    val isAuthenticated: StateFlow<Boolean> = authRepo.isAuthenticated

    // Chat Loading & Streaming State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Voice & Speech Engine
    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _voiceTranscript = MutableStateFlow("")
    val voiceTranscript: StateFlow<String> = _voiceTranscript.asStateFlow()

    init {
        initTts(application)
        initSpeechRecognizer(application)
        viewModelScope.launch {
            val initialId = chatRepo.getOrCreateInitialConversation()
            _activeConversationId.value = initialId
            observeMessages(initialId)
            // Seed initial memory for Soaib Ali if none exists
            seedDefaultMemories()
            seedDefaultTasks()
        }
    }

    private fun initTts(app: Application) {
        tts = TextToSpeech(app) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    private fun initSpeechRecognizer(app: Application) {
        if (SpeechRecognizer.isRecognitionAvailable(app)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(app).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) { _isListening.value = true }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() { _isListening.value = false }
                    override fun onError(error: Int) { _isListening.value = false }
                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val recognized = matches[0]
                            _voiceTranscript.value = recognized
                            sendVoiceQuery(recognized)
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            _voiceTranscript.value = matches[0]
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    private suspend fun seedDefaultMemories() {
        memoryRepo.saveMemory("Name", "Soaib Ali", "Identity")
        memoryRepo.saveMemory("Role", "Lead Technologist & Developer", "Career")
        memoryRepo.saveMemory("Style", "Clean, concise, and structured explanations", "Preference")
    }

    private suspend fun seedDefaultTasks() {
        taskRepo.addTask("Explore SOANEX AI features", "Test Chat, Image Generator, and Voice modes", "HIGH", "Today, 6:00 PM")
        taskRepo.addTask("Setup Personal Memory profile", "Add custom instructions and preferred tone", "MEDIUM", "Tomorrow, 10:00 AM")
    }

    fun navigateTo(destination: NavDestination) {
        _currentDestination.value = destination
    }

    fun selectConversation(id: String) {
        _activeConversationId.value = id
        _currentDestination.value = NavDestination.AI_ASSISTANT
        observeMessages(id)
    }

    fun createNewChat() {
        viewModelScope.launch {
            val newId = chatRepo.createNewConversation()
            _activeConversationId.value = newId
            _currentDestination.value = NavDestination.AI_ASSISTANT
            observeMessages(newId)
        }
    }

    fun renameConversation(id: String, newTitle: String) {
        viewModelScope.launch {
            chatRepo.renameConversation(id, newTitle)
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            chatRepo.deleteConversation(id)
            if (_activeConversationId.value == id) {
                val next = conversations.value.firstOrNull { it.id != id }
                if (next != null) {
                    selectConversation(next.id)
                } else {
                    createNewChat()
                }
            }
        }
    }

    private fun observeMessages(convoId: String) {
        viewModelScope.launch {
            chatRepo.getMessages(convoId).collect { msgs ->
                _currentMessages.value = msgs
            }
        }
    }

    fun sendMessage(content: String, imageBase64: String? = null) {
        if (content.isBlank() && imageBase64 == null) return
        val convoId = _activeConversationId.value ?: return

        viewModelScope.launch {
            _isGenerating.value = true
            try {
                val enabledMems = memoryRepo.getEnabledMemories()
                val responseMsg = chatRepo.sendMessage(
                    conversationId = convoId,
                    content = content,
                    imageBase64 = imageBase64,
                    preferences = _aiPreferences.value,
                    memories = enabledMems
                )
                if (_currentDestination.value == NavDestination.VOICE && !_isSpeaking.value) {
                    speakText(responseMsg.content)
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun startListening() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "Voice input not supported or permission required", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    fun sendVoiceQuery(query: String) {
        if (query.isNotBlank()) {
            sendMessage(query)
        }
    }

    fun speakText(text: String) {
        val cleanText = text.replace(Regex("[#*`_-]"), "")
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "soanex_tts")
        _isSpeaking.value = true
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun updateAiPreferences(prefs: AiPreferences) {
        _aiPreferences.value = prefs
    }

    // Image Generator
    fun generateImage(prompt: String, style: String, aspectRatio: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            imageRepo.generateImage(prompt, style, aspectRatio)
            _isGenerating.value = false
        }
    }

    fun deleteImage(id: String) {
        viewModelScope.launch {
            imageRepo.deleteImage(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }
}
