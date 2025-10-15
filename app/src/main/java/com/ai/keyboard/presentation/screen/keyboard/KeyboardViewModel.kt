package com.ai.keyboard.presentation.screen.keyboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.domain.model.KeyboardState
import com.ai.keyboard.domain.model.KeyboardTheme
import com.ai.keyboard.domain.repository.SettingsRepository
import com.ai.keyboard.domain.usecase.FixGrammarUseCase
import com.ai.keyboard.domain.usecase.GetAiWritingAssistanceUseCase
import com.ai.keyboard.domain.usecase.GetSuggestionsUseCase
import com.ai.keyboard.domain.usecase.GetTranslateUseCase
import com.ai.keyboard.domain.usecase.GetWordToneUseCase
import com.ai.keyboard.domain.usecase.RephraseContentUseCase
import com.ai.keyboard.presentation.model.AIWritingAssistanceType
import com.ai.keyboard.presentation.model.ActionButtonType
import com.ai.keyboard.presentation.model.LanguageType
import com.ai.keyboard.presentation.model.WordToneType
import com.ai.keyboard.presentation.service.KeyboardBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeyboardViewModel(
    private val getSuggestionsUseCase: GetSuggestionsUseCase,
    private val rephraseContentUseCase: RephraseContentUseCase,
    private val settingsRepository: SettingsRepository,
    private val fixGrammarUseCase: FixGrammarUseCase,
    private val getWordToneUseCase: GetWordToneUseCase,
    private val getAiWritingAssistanceUseCase: GetAiWritingAssistanceUseCase,
    private val getTranslateUseCase: GetTranslateUseCase,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(KeyboardUIState())
    val uiState: StateFlow<KeyboardUIState> = _uiState.asStateFlow()

    private val textChangeChannel = Channel<String>(Channel.CONFLATED)

    private var onTextChangeListener: ((String, Int) -> Unit)? = null
    private var onCursorChangeListener: ((Int) -> Unit)? = null
    private var onTextSelectAndDeleteListener: ((Int) -> Unit)? = null
    private var onKeyPressListener: (() -> Unit)? = null
    private var onImeActionListener: ((Int) -> Unit)? = null

    private var speechRecognizer: SpeechRecognizer? = null

    init {
        observeSettings()
        setupSuggestionDebouncing()
    }

    fun initializeText(text: String, cursorPosition: Int) {
        updateKeyboardState {
            copy(
                currentText = text,
                cursorPosition = cursorPosition.coerceIn(0, text.length)
            )
        }
    }

    fun resetText() {
        updateKeyboardState {
            copy(
                currentText = "",
                cursorPosition = 0
            )
        }
    }

    fun syncWithInputConnection(text: String, cursorPosition: Int) {
        updateKeyboardState {
            copy(
                currentText = text,
                cursorPosition = cursorPosition.coerceIn(0, text.length)
            )
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSuggestionDebouncing() {
        viewModelScope.launch {
            textChangeChannel.receiveAsFlow()
                .debounce(200)
                .distinctUntilChanged()
                .collect { text ->
                    fetchSuggestions(text)
                }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.getTheme().collect { theme ->
                updateKeyboardState { copy(theme = theme) }
            }
        }

        viewModelScope.launch {
            settingsRepository.isHapticEnabled().collect { enabled ->
                updateKeyboardState { copy(isHapticEnabled = enabled) }
            }
        }

        viewModelScope.launch {
            settingsRepository.isSoundEnabled().collect { enabled ->
                updateKeyboardState { copy(isSoundEnabled = enabled) }
            }
        }

        viewModelScope.launch {
            settingsRepository.isNumberRowEnabled().collect { enabled ->
                updateKeyboardState { copy(isNumberRowEnabled = enabled) }
            }
        }
    }

    fun setOnTextChangeListener(listener: (String, Int) -> Unit) {
        onTextChangeListener = listener
    }

    fun setOnCursorChangeListener(listener: (Int) -> Unit) {
        onCursorChangeListener = listener
    }

    fun setOnTextSelectAndDeleteListener(listener: (Int) -> Unit) {
        onTextSelectAndDeleteListener = listener
    }

    fun setOnKeyPressListener(listener: () -> Unit) {
        onKeyPressListener = listener
    }

    fun setOnImeActionListener(listener: (Int) -> Unit) {
        onImeActionListener = listener
    }

    fun setImeAction(imeAction: Int) {
        updateKeyboardState { copy(imeAction = imeAction) }
    }

    fun handleIntent(intent: KeyboardIntent) {
        when (intent) {
            is KeyboardIntent.KeyPressed -> handleKeyPress(intent.action)
            is KeyboardIntent.SuggestionSelected -> insertSuggestion(intent.suggestion)
            is KeyboardIntent.ShiftPressed -> toggleShift()
            is KeyboardIntent.SymbolPressed -> toggleSymbol()
            is KeyboardIntent.ThemeChanged -> changeTheme(intent.theme)
            is KeyboardIntent.ExtendedSymbolPressed -> toggleExtendedSymbol()
            is KeyboardIntent.AlphabetPressed -> toggleToNormalKeyboard()
            is KeyboardIntent.ToggleHaptic -> toggleHaptic()
            is KeyboardIntent.ToggleSound -> toggleSound()
            is KeyboardIntent.CursorPositionChanged -> updateCursorPosition(intent.position)
            is KeyboardIntent.ToggleNumerRow -> toggleNumerRow()
            is KeyboardIntent.EmojiPressed -> toggleEmoji()
            is KeyboardIntent.FixGrammarPressed -> toggleFixGrammar()
            is KeyboardIntent.RewritePressed -> toggleRewrite()
            is KeyboardIntent.AiAssistancePressed -> toggleAiAssistance()
            is KeyboardIntent.TranslatePressed -> toggleTranslate()
            is KeyboardIntent.VoiceToTextPressed -> toggleVoiceToText()
        }
    }

    private fun handleKeyPress(action: KeyAction) {
        val currentState = _uiState.value.keyboardState
        var currentText = currentState.currentText
        var cursorPosition = currentState.cursorPosition
        var textChanged = true
        var characterToCommit = ""

        when (action) {
            is KeyAction.Character -> {
                val char = when (currentState.mode) {
                    KeyboardMode.UPPERCASE, KeyboardMode.CAPS_LOCK -> action.char.uppercase()
                    else -> action.char
                }

                currentText = StringBuilder(currentText).apply {
                    insert(cursorPosition, char)
                }.toString()
                cursorPosition += char.length
                characterToCommit = char
            }

            is KeyAction.Backspace -> {
                if (cursorPosition > 0 && currentText.isNotEmpty()) {
                    currentText = StringBuilder(currentText).apply {
                        deleteCharAt(cursorPosition - 1)
                    }.toString()
                    cursorPosition--
                    onTextChangeListener?.invoke("BACKSPACE", cursorPosition)
                    textChanged = false
                } else {
                    textChanged = false
                }
            }

            is KeyAction.Enter -> {
                characterToCommit = "\n"
                currentText = StringBuilder(currentText).insert(cursorPosition, characterToCommit).toString()
                cursorPosition += characterToCommit.length
            }

            is KeyAction.ImeAction -> {
                onImeActionListener?.invoke(action.action)
                textChanged = false
            }

            is KeyAction.Space -> {
                currentText = StringBuilder(currentText).apply {
                    insert(cursorPosition, " ")
                }.toString()
                cursorPosition += 1
                characterToCommit = " "
            }

            is KeyAction.MoveCursor -> {
                val newPosition = cursorPosition + action.amount
                cursorPosition = newPosition.coerceIn(0, currentText.length)
                textChanged = false
            }

            is KeyAction.SelectAndDelete -> {
                val newPosition = cursorPosition + action.amount
                cursorPosition = newPosition.coerceIn(0, currentText.length)
                onTextSelectAndDeleteListener?.invoke(action.amount)
                textChanged = false
            }

            KeyAction.Shift, KeyAction.Symbol, KeyAction.Emoji, KeyAction.ExtendedSymbol -> {
                textChanged = false
            }

            is KeyAction.InsertSuggestion -> {

            }

        }

        updateKeyboardState {
            copy(
                currentText = currentText,
                cursorPosition = cursorPosition
            )
        }

        if (textChanged && characterToCommit.isNotEmpty()) {
            onTextChangeListener?.invoke(characterToCommit, cursorPosition)
            textChangeChannel.trySend(currentText)
        } else if (!textChanged && action !is KeyAction.Backspace) {
            onCursorChangeListener?.invoke(cursorPosition)
        }

        onKeyPressListener?.invoke()

        if (currentState.mode == KeyboardMode.UPPERCASE && action is KeyAction.Character) {
            updateMode(KeyboardMode.LOWERCASE)
        }
    }

    private fun updateCursorPosition(position: Int) {
        val currentText = _uiState.value.keyboardState.currentText
        val validPosition = position.coerceIn(0, currentText.length)
        updateKeyboardState { copy(cursorPosition = validPosition) }
    }

    private fun insertSuggestion(suggestion: String) {
        val currentState = _uiState.value.keyboardState
        val currentText = currentState.currentText
        val cursorPos = currentState.cursorPosition

        println("current: $currentText")

        val trimmedText = currentText.trimEnd()

        val lastSpaceIndex = trimmedText.lastIndexOf(' ')

        val textWithoutLastWord = if (lastSpaceIndex != -1) {
            trimmedText.substring(0, lastSpaceIndex + 1)
        } else {
            ""
        }

        // Replace current word with suggestion
        val newText = StringBuilder(currentText).apply {
            delete(wordStart, cursorPos)
            insert(wordStart, "$suggestion ")
        }.toString()

        val newCursorPos = wordStart + suggestion.length + 1

        updateKeyboardState {
            copy(
                currentText = newText,
                cursorPosition = newCursorPos
            )
        }

        onTextChangeListener?.invoke(newText, newCursorPos)
        onKeyPressListener?.invoke()
        textChangeChannel.trySend(newText)
    }

    private fun toggleShift() {
        val currentMode = _uiState.value.keyboardState.mode
        val newMode = when (currentMode) {
            KeyboardMode.LOWERCASE -> KeyboardMode.UPPERCASE
            KeyboardMode.UPPERCASE -> KeyboardMode.CAPS_LOCK
            KeyboardMode.CAPS_LOCK -> KeyboardMode.LOWERCASE
            else -> currentMode
        }
        updateMode(newMode)
    }

    private fun toggleNumerRow() {
        val currentState = _uiState.value.keyboardState
        val newValue = !currentState.isNumberRowEnabled
        updateKeyboardState { copy(isNumberRowEnabled = newValue) }
        viewModelScope.launch {
            settingsRepository.saveNumberRowEnabled(newValue)
        }
    }

    private fun toggleSymbol() {
        val currentMode = _uiState.value.keyboardState.mode
        val newMode = if (currentMode == KeyboardMode.SYMBOLS) {
            KeyboardMode.LOWERCASE
        } else {
            KeyboardMode.SYMBOLS
        }
        updateMode(newMode)
    }

    private fun toggleEmoji() {
        val currentMode = _uiState.value.keyboardState.mode
        val newMode = if (currentMode == KeyboardMode.EMOJI) {
            KeyboardMode.LOWERCASE
        } else {
            KeyboardMode.EMOJI
        }
        updateMode(newMode)
    }


    private fun toggleExtendedSymbol() {
        val currentMode = _uiState.value.keyboardState.mode
        val newMode = when (currentMode) {
            KeyboardMode.EXTENDED_SYMBOLS -> KeyboardMode.SYMBOLS
            else -> KeyboardMode.EXTENDED_SYMBOLS
        }
        updateMode(newMode)
    }

    private fun toggleToNormalKeyboard() {
        updateMode(KeyboardMode.LOWERCASE)
    }


    private fun toggleFixGrammar() {
        updateMode(KeyboardMode.FIX_GRAMMAR)
    }
    private fun toggleRewrite() {
        updateMode(KeyboardMode.REWRITE)
    }
    private fun toggleAiAssistance() {
        updateMode(KeyboardMode.AI_ASSISTANCE)
    }
    private fun toggleTranslate() {
        updateMode(KeyboardMode.TRANSLATE)
    }
    private fun updateMode(mode: KeyboardMode) {
        updateKeyboardState { copy(mode = mode) }
    }

    private fun fetchSuggestions(text: String) {
        if (text.isBlank()) {
            _uiState.update { it.copy(suggestions = emptyList()) }
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(isLoading = true) }

            getSuggestionsUseCase(text)
                .onSuccess { suggestions ->
                    _uiState.update { state ->
                        state.copy(
                            suggestions = suggestions,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    private fun changeTheme(theme: KeyboardTheme) {
        viewModelScope.launch {
            settingsRepository.saveTheme(theme)
        }
    }

    private fun toggleHaptic() {
        viewModelScope.launch {
            val current = _uiState.value.keyboardState.isHapticEnabled
            settingsRepository.saveHapticEnabled(!current)
        }
    }

    private fun toggleSound() {
        viewModelScope.launch {
            val current = _uiState.value.keyboardState.isSoundEnabled
            settingsRepository.saveSoundEnabled(!current)
        }
    }

    private fun updateKeyboardState(update: KeyboardState.() -> KeyboardState) {
        _uiState.update { state ->
            state.copy(keyboardState = state.keyboardState.update())
        }
    }

    fun updateInputFieldText(text: String) {
        _uiState.update { current ->
            current.copy(inputFieldText = text)
        }
    }

    fun getGrammar() {
        val currentState = _uiState.value
        var currentText = currentState.inputFieldText
        viewModelScope.launch {
            val result = fixGrammarUseCase(
                content = currentText,
                language = _uiState.value.language.name,
                action = _uiState.value.selectedAction.name
            )

            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.value = _uiState.value.copy(
                        correctedText = result.data,
                        error = ""
                    )
                }

                is ResultWrapper.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message
                    )
                }

                is ResultWrapper.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun getWordTone() {
        val currentState = _uiState.value
        var currentText = currentState.inputFieldText
        viewModelScope.launch {
            val result = getWordToneUseCase(
                content = currentText,
                language = _uiState.value.language.name,
                action = _uiState.value.selectedAction.name
            )

            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.value = _uiState.value.copy(
                        correctedText = result.data,
                        error = ""
                    )
                }

                is ResultWrapper.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message
                    )
                }

                is ResultWrapper.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun getAiAssistance() {
        val currentState = _uiState.value
        var currentText = currentState.inputFieldText
        viewModelScope.launch {
            val result = getAiWritingAssistanceUseCase(
                content = currentText,
                language = _uiState.value.language.name,
                action = _uiState.value.selectedAction.name
            )

            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.value = _uiState.value.copy(
                        correctedText = result.data,
                        error = ""
                    )
                }

                is ResultWrapper.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message
                    )
                }

                is ResultWrapper.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }
    fun getTranslate() {
        val currentState = _uiState.value
        var currentText = currentState.inputFieldText
        viewModelScope.launch {
            val result = getTranslateUseCase(
                content = currentText,
                language = _uiState.value.language.name
            )

            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.value = _uiState.value.copy(
                        correctedText = result.data,
                        error = ""
                    )
                }

                is ResultWrapper.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message
                    )
                }

                is ResultWrapper.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }
    fun onLanguageSelected(language: LanguageType) {
        _uiState.value = _uiState.value.copy(
            language = language
        )
    }

    fun onSelectedActionChange(action: ActionButtonType) {
        _uiState.value = _uiState.value.copy(
            selectedAction = action
        )
    }
    fun onSelectedAiActionChange(action: AIWritingAssistanceType) {
        _uiState.value = _uiState.value.copy(
            selectedAiAction = action
        )
    }
    fun onSelectedWordActionChange(action: WordToneType) {
        _uiState.value = _uiState.value.copy(
            selectedWordAction = action
        )
    }
    fun replaceCurrentInputWith(newText: String) {
        KeyboardBridge.ime?.replaceInputFieldText(newText)
    }

    private fun toggleVoiceToText() {
        val currentState = _uiState.value
        if (currentState.isListening) {
            stopVoiceRecognition()
        } else {
            startVoiceRecognition()
        }
    }

    private fun startVoiceRecognition() {
        // Check for audio permission first
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            
            // Send broadcast to request permission
            val intent = Intent("com.ai.keyboard.REQUEST_AUDIO_PERMISSION")
            context.sendBroadcast(intent)
            
            _uiState.update { it.copy(
                needsAudioPermission = true, 
                error = "Requesting microphone permission..."
            ) }
            return
        }

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _uiState.update { it.copy(error = "Speech recognition not available") }
            return
        }

        _uiState.update { it.copy(isListening = true, error = null, needsAudioPermission = false) }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    // Speech recognition is ready
                }

                override fun onBeginningOfSpeech() {
                    // User started speaking
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // Volume level changed
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    // Audio buffer received
                }

                override fun onEndOfSpeech() {
                    // User stopped speaking
                }

                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                        SpeechRecognizer.ERROR_NETWORK -> "Network error"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech input"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                        SpeechRecognizer.ERROR_SERVER -> "Server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                        else -> "Unknown error"
                    }
                    _uiState.update { it.copy(isListening = false, error = errorMessage) }
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val recognizedText = matches?.firstOrNull() ?: ""
                    
                    if (recognizedText.isNotEmpty()) {
                        // Insert the recognized text at cursor position
                        val currentState = _uiState.value.keyboardState
                        val currentText = currentState.currentText
                        val cursorPos = currentState.cursorPosition
                        
                        val newText = StringBuilder(currentText).apply {
                            insert(cursorPos, recognizedText)
                        }.toString()
                        
                        val newCursorPos = cursorPos + recognizedText.length
                        
                        updateKeyboardState {
                            copy(
                                currentText = newText,
                                cursorPosition = newCursorPos
                            )
                        }
                        
                        _uiState.update { 
                            it.copy(
                                isListening = false, 
                                voiceToTextResult = recognizedText,
                                error = null
                            ) 
                        }
                        
                        // Notify the input connection
                        onTextChangeListener?.invoke(recognizedText, newCursorPos)
                        onKeyPressListener?.invoke()
                        textChangeChannel.trySend(newText)
                    } else {
                        _uiState.update { it.copy(isListening = false, error = "No speech recognized") }
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    // Partial results received
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                    // Reserved for future use
                }
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer?.startListening(intent)
    }

    private fun stopVoiceRecognition() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        _uiState.update { it.copy(isListening = false) }
    }

    override fun onCleared() {
        super.onCleared()
        stopVoiceRecognition()
    }
}