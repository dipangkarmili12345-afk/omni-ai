package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferences
import com.example.data.local.UserPreferencesManager
import com.example.data.model.AiModelType
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.CompareSession
import com.example.data.model.ToolType
import com.example.data.repository.AiRepository
import com.example.data.repository.ChatRepository
import com.example.data.service.AdsManager
import com.example.data.service.VoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OmniViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val chatRepo = ChatRepository(db.chatDao())
    val aiRepo = AiRepository()
    val prefsManager = UserPreferencesManager(application)
    val voiceManager = VoiceManager(application)
    val adsManager = AdsManager()

    val userPreferences: StateFlow<UserPreferences> = prefsManager.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    val allSessions: StateFlow<List<ChatSession>> = chatRepo.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId: StateFlow<String?> = _activeSessionId.asStateFlow()

    val activeMessages: StateFlow<List<ChatMessage>> = _activeSessionId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else chatRepo.getMessagesForSession(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedModel = MutableStateFlow(AiModelType.GEMINI)
    val selectedModel: StateFlow<AiModelType> = _selectedModel.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _compareSession = MutableStateFlow<CompareSession?>(null)
    val compareSession: StateFlow<CompareSession?> = _compareSession.asStateFlow()

    private val _isComparing = MutableStateFlow(false)
    val isComparing: StateFlow<Boolean> = _isComparing.asStateFlow()

    private val _activeTool = MutableStateFlow<ToolType?>(null)
    val activeTool: StateFlow<ToolType?> = _activeTool.asStateFlow()

    private val _toolOutput = MutableStateFlow("")
    val toolOutput: StateFlow<String> = _toolOutput.asStateFlow()

    private val _isToolLoading = MutableStateFlow(false)
    val isToolLoading: StateFlow<Boolean> = _isToolLoading.asStateFlow()

    private val _generatedImageUrl = MutableStateFlow<String?>(null)
    val generatedImageUrl: StateFlow<String?> = _generatedImageUrl.asStateFlow()

    private val _userNotice = MutableStateFlow<String?>(null)
    val userNotice: StateFlow<String?> = _userNotice.asStateFlow()

    private val _voiceChatActive = MutableStateFlow(false)
    val voiceChatActive: StateFlow<Boolean> = _voiceChatActive.asStateFlow()

    init {
        viewModelScope.launch {
            prefsManager.checkAndResetDailyLimit()
        }
        viewModelScope.launch {
            prefsManager.userPreferencesFlow.collect { prefs ->
                aiRepo.backendUrl = prefs.backendUrl
            }
        }
    }

    fun selectModel(model: AiModelType) {
        _selectedModel.value = model
    }

    fun selectSession(sessionId: String) {
        _activeSessionId.value = sessionId
    }

    fun startNewChat(model: AiModelType = _selectedModel.value) {
        viewModelScope.launch {
            val id = chatRepo.createNewSession("New Chat", model.id)
            _selectedModel.value = model
            _activeSessionId.value = id
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            chatRepo.deleteSession(sessionId)
            if (_activeSessionId.value == sessionId) {
                _activeSessionId.value = null
            }
        }
    }

    fun clearAllChatHistory() {
        viewModelScope.launch {
            chatRepo.clearAllHistory()
            _activeSessionId.value = null
        }
    }

    fun dismissNotice() {
        _userNotice.value = null
    }

    fun sendMessage(text: String, onLimitReached: () -> Unit = {}) {
        val trimmed = text.trim()
        if (trimmed.isBlank() || _isGenerating.value) return

        viewModelScope.launch {
            val allowed = prefsManager.incrementMessageUsage()
            if (!allowed) {
                _userNotice.value = "Daily quota reached! Watch a rewarded ad to get +10 messages or upgrade to Premium."
                onLimitReached()
                return@launch
            }

            var sessionId = _activeSessionId.value
            if (sessionId == null) {
                sessionId = chatRepo.createNewSession("New Chat", _selectedModel.value.id)
                _activeSessionId.value = sessionId
            }

            // Save user message
            chatRepo.saveMessage(
                sessionId = sessionId,
                role = "user",
                modelId = _selectedModel.value.id,
                content = trimmed
            )

            _isGenerating.value = true

            try {
                val history = activeMessages.value
                val response = aiRepo.generateResponse(_selectedModel.value, trimmed, history)

                chatRepo.saveMessage(
                    sessionId = sessionId,
                    role = "model",
                    modelId = _selectedModel.value.id,
                    content = response
                )

                // Maybe trigger interstitial ad for free users
                adsManager.triggerActionForInterstitial(userPreferences.value.isPremium)

            } catch (e: Exception) {
                chatRepo.saveMessage(
                    sessionId = sessionId,
                    role = "model",
                    modelId = _selectedModel.value.id,
                    content = "Error generating response: ${e.localizedMessage ?: "Unknown error"}. Switched to Smart Offline mode."
                )
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun runCompare(prompt: String, onLimitReached: () -> Unit = {}) {
        val trimmed = prompt.trim()
        if (trimmed.isBlank() || _isComparing.value) return

        viewModelScope.launch {
            val allowed = prefsManager.incrementMessageUsage()
            if (!allowed) {
                _userNotice.value = "Daily limit reached! Watch a short ad for +10 messages."
                onLimitReached()
                return@launch
            }

            _isComparing.value = true
            _compareSession.value = CompareSession(prompt = trimmed)

            try {
                val results = aiRepo.compareModels(trimmed, AiModelType.entries)
                _compareSession.value = CompareSession(prompt = trimmed, results = results)
                adsManager.triggerActionForInterstitial(userPreferences.value.isPremium)
            } catch (e: Exception) {
                _userNotice.value = "Comparison failed: ${e.localizedMessage}"
            } finally {
                _isComparing.value = false
            }
        }
    }

    fun openTool(tool: ToolType) {
        _activeTool.value = tool
        _toolOutput.value = ""
        _generatedImageUrl.value = null
    }

    fun closeTool() {
        _activeTool.value = null
        _toolOutput.value = ""
        _generatedImageUrl.value = null
    }

    fun executeTool(
        tool: ToolType,
        param1: String,
        param2: String = "",
        param3: String = "",
        param4: String = "",
        param5: String = "",
        param6: String = "",
        param7: String = ""
    ) {
        viewModelScope.launch {
            val allowed = prefsManager.incrementMessageUsage()
            if (!allowed) {
                _userNotice.value = "Daily quota reached! Watch a short ad to earn +10 messages."
                return@launch
            }

            _isToolLoading.value = true
            try {
                when (tool) {
                    ToolType.IMAGE_GENERATOR -> {
                        val imgUrl = aiRepo.generateImage(prompt = param1, style = param2, aspectRatio = param3)
                        _generatedImageUrl.value = imgUrl
                        _toolOutput.value = "Image generated for prompt: \"$param1\" with style $param2 ($param3)"
                    }
                    ToolType.CODE_WRITER -> {
                        val code = aiRepo.generateCode(language = param1, taskPrompt = param2)
                        _toolOutput.value = code
                    }
                    ToolType.TRANSLATOR -> {
                        val translation = aiRepo.translate(text = param1, sourceLang = param2, targetLang = param3)
                        _toolOutput.value = translation
                    }
                    ToolType.SUMMARIZER -> {
                        val summary = aiRepo.summarize(text = param1, format = param2)
                        _toolOutput.value = summary
                    }
                    ToolType.ESSAY_WRITER -> {
                        val essay = aiRepo.writeEssay(topic = param1, tone = param2, length = param3)
                        _toolOutput.value = essay
                    }
                    ToolType.RESUME_BUILDER -> {
                        val resume = aiRepo.buildResume(
                            name = param1,
                            role = param2,
                            contact = param3,
                            summary = param4,
                            experience = param5,
                            education = param6,
                            skills = param7
                        )
                        _toolOutput.value = resume
                    }
                    ToolType.EMAIL_WRITER -> {
                        val email = aiRepo.writeEmail(
                            purpose = param1,
                            recipient = param2,
                            keyPoints = param3,
                            tone = param4
                        )
                        _toolOutput.value = email
                    }
                    ToolType.VOICE_CHAT -> {
                        openVoiceChat()
                    }
                }
                adsManager.triggerActionForInterstitial(userPreferences.value.isPremium)
            } catch (e: Exception) {
                _toolOutput.value = "Failed to run tool: ${e.localizedMessage}"
            } finally {
                _isToolLoading.value = false
            }
        }
    }

    fun openVoiceChat() {
        _voiceChatActive.value = true
    }

    fun closeVoiceChat() {
        _voiceChatActive.value = false
        voiceManager.stopListening()
        voiceManager.stopSpeaking()
    }

    fun sendVoicePrompt(text: String) {
        if (text.isBlank() || _isGenerating.value) return
        viewModelScope.launch {
            prefsManager.incrementMessageUsage()
            _isGenerating.value = true
            try {
                val response = aiRepo.generateResponse(_selectedModel.value, text, emptyList())
                voiceManager.speak(response)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun watchRewardedAd() {
        adsManager.showRewardedAd { reward ->
            viewModelScope.launch {
                prefsManager.addBonusMessages(reward)
                _userNotice.value = "🎉 Reward Granted! You received +$reward bonus AI messages."
            }
        }
    }

    fun upgradeSubscription(planName: String) {
        viewModelScope.launch {
            prefsManager.setPremium(true, planName)
            _userNotice.value = "🚀 Welcome to Omni AI Premium! Unlimited messages & zero ads activated."
        }
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            prefsManager.setPremium(false, "Free")
            _userNotice.value = "Subscription downgraded to Free Tier."
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            val prefs = prefsManager.userPreferencesFlow.first()
            if (prefs.isPremium) {
                _userNotice.value = "Google Play: Active subscription found (${prefs.premiumPlan} Plan)."
            } else {
                _userNotice.value = "Google Play: No active subscriptions found. You are currently on the Free Tier."
            }
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            prefsManager.setThemeMode(theme)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            prefsManager.setLanguage(lang)
        }
    }

    fun setBackendUrl(url: String) {
        viewModelScope.launch {
            val cleanUrl = url.trim()
            prefsManager.setBackendUrl(cleanUrl)
            aiRepo.backendUrl = cleanUrl
            _userNotice.value = if (cleanUrl.isBlank()) "Backend URL cleared. Using offline hybrid mode." else "Connected: $cleanUrl"
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}

class OmniViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OmniViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OmniViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
