package com.ai.keyboard.presentation.screen.keyboard

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.keyboard.core.clipboard.AndroidClipboardManager
import com.ai.keyboard.core.service.ServiceDataPass
import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.domain.model.ClipboardItem
import com.ai.keyboard.domain.model.ClipboardState
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.domain.model.KeyboardState
import com.ai.keyboard.domain.model.KeyboardTheme
import com.ai.keyboard.domain.repository.SettingsRepository
import com.ai.keyboard.domain.usecase.FixGrammarUseCase
import com.ai.keyboard.domain.usecase.GetAiWritingAssistanceUseCase
import com.ai.keyboard.domain.usecase.GetClipboardItemsUseCase
import com.ai.keyboard.domain.usecase.GetSuggestionsUseCase
import com.ai.keyboard.domain.usecase.GetTranslateUseCase
import com.ai.keyboard.domain.usecase.GetWordToneUseCase
import com.ai.keyboard.domain.usecase.ManageClipboardUseCase
import com.ai.keyboard.domain.usecase.QuickReplyUseCase
import com.ai.keyboard.domain.usecase.RephraseContentUseCase
import com.ai.keyboard.presentation.model.AIWritingAssistanceType
import com.ai.keyboard.presentation.model.ActionButtonType
import com.ai.keyboard.presentation.model.LanguageType
import com.ai.keyboard.presentation.model.QuickReplyModule
import com.ai.keyboard.presentation.model.WordToneType
import com.ai.keyboard.presentation.service.KeyboardBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class KeyboardViewModel(
    private val getSuggestionsUseCase: GetSuggestionsUseCase,
    private val rephraseContentUseCase: RephraseContentUseCase,
    private val settingsRepository: SettingsRepository,
    private val fixGrammarUseCase: FixGrammarUseCase,
    private val quickReplyUseCase: QuickReplyUseCase,
    private val getWordToneUseCase: GetWordToneUseCase,
    private val getAiWritingAssistanceUseCase: GetAiWritingAssistanceUseCase,
    private val getTranslateUseCase: GetTranslateUseCase,
    private val getClipboardItemsUseCase: GetClipboardItemsUseCase,
    private val manageClipboardUseCase: ManageClipboardUseCase,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(KeyboardUIState())
    val uiState: StateFlow<KeyboardUIState> = _uiState.asStateFlow()

    private val textChangeChannel = Channel<String>(Channel.CONFLATED)
    private var speechRecognizer: SpeechRecognizer? = null
    private val clipboardManager = AndroidClipboardManager(context)

    private var onTextChangeListener: ((String, Int) -> Unit)? = null
    private var onCursorChangeListener: ((Int) -> Unit)? = null
    private var onTextSelectAndDeleteListener: ((Int) -> Unit)? = null
    private var onKeyPressListener: (() -> Unit)? = null
    private var onImeActionListener: ((Int) -> Unit)? = null

    private data class TextHistory(val text: String, val cursorPosition: Int)
    private val undoStack = mutableListOf<TextHistory>()
    private val redoStack = mutableListOf<TextHistory>()

    init {
        observeSettings()
        setupSuggestionDebouncing()
        observeClipboard()
    }

    fun initializeText(text: String, cursorPosition: Int) {
        undoStack.clear()
        redoStack.clear()
        updateKeyboardState {
            copy(
                currentText = text,
                cursorPosition = cursorPosition.coerceIn(0, text.length)
            )
        }
    }

    fun resetText() {
        undoStack.clear()
        redoStack.clear()
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
            is KeyboardIntent.GetQuickReply -> toggleQuickReply()
            is KeyboardIntent.RewritePressed -> toggleRewrite()
            is KeyboardIntent.AiAssistancePressed -> toggleAiAssistance()
            is KeyboardIntent.TranslatePressed -> toggleTranslate()
            is KeyboardIntent.UndoPressed -> handleUndo()
            is KeyboardIntent.RedoPressed -> handleRedo()

            // Clipboard intents
            is KeyboardIntent.ClipboardOpen -> toggleClipboardOpen()
            is KeyboardIntent.ToggleClipboardEnabled -> toggleClipboardEnabled()
            is KeyboardIntent.ToggleClipboardEditMode -> toggleClipboardEditMode()
            is KeyboardIntent.ClipboardItemSelected -> selectClipboardItem(intent.item)
            is KeyboardIntent.ClipboardItemToggleSelect -> toggleClipboardItemSelection(intent.item)
            is KeyboardIntent.DeleteSelectedClipboardItems -> deleteSelectedClipboardItems()
            is KeyboardIntent.VoiceToTextPressed -> startVoiceRecognition()
            is KeyboardIntent.PasteFromClipboard -> pasteFromClipboard()
        }
    }

    private fun addStateToUndoStack(state: TextHistory) {
        if (undoStack.lastOrNull()?.text == state.text) return
        undoStack.add(state)
        redoStack.clear()
        if (undoStack.size > 20) { // Limit undo history
            undoStack.removeAt(0)
        }
    }

    private fun handleKeyPress(action: KeyAction) {
        val currentState = _uiState.value.keyboardState
        val originalState = TextHistory(currentState.currentText, currentState.cursorPosition)
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
                if (currentText.isEmpty()) addStateToUndoStack(originalState)
                currentText = StringBuilder(currentText).apply {
                    insert(cursorPosition, char)
                }.toString()
                cursorPosition += char.length
                characterToCommit = char
            }

            is KeyAction.Backspace -> {
                if (cursorPosition > 0 && currentText.isNotEmpty()) {
                    val deletedChar = currentText[cursorPosition - 1]
                    if (deletedChar.isWhitespace() || !deletedChar.isLetterOrDigit()) {
                        addStateToUndoStack(originalState)
                    }
                    currentText = StringBuilder(currentText).apply {
                        deleteCharAt(cursorPosition - 1)
                    }.toString()
                    cursorPosition--
                    // For backspace, we'll handle it differently
                    onTextChangeListener?.invoke("BACKSPACE", cursorPosition)
                    textChanged = false
                } else {
                    textChanged = false
                }
            }

            is KeyAction.Enter -> {
                addStateToUndoStack(originalState)
                characterToCommit = ""
                currentText = StringBuilder(currentText).insert(cursorPosition, characterToCommit).toString()
            }

            is KeyAction.ImeAction -> {
                addStateToUndoStack(originalState)
                onImeActionListener?.invoke(action.action)
                textChanged = false
            }

            is KeyAction.Space -> {
                addStateToUndoStack(originalState)
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

    private fun handleUndo() {
        if (undoStack.isNotEmpty()) {
            val currentState = _uiState.value.keyboardState
            if (redoStack.lastOrNull()?.text != currentState.currentText) {
                redoStack.add(TextHistory(currentState.currentText, currentState.cursorPosition))
            }

            val previousState = undoStack.removeAt(undoStack.lastIndex)
            replaceCurrentInputWith(previousState.text)
            updateKeyboardState {
                copy(
                    currentText = previousState.text,
                    cursorPosition = previousState.cursorPosition
                )
            }
            onCursorChangeListener?.invoke(previousState.cursorPosition)
        }
    }

    private fun handleRedo() {
        if (redoStack.isNotEmpty()) {
            val currentState = _uiState.value.keyboardState
            if (undoStack.lastOrNull()?.text != currentState.currentText) {
                undoStack.add(TextHistory(currentState.currentText, currentState.cursorPosition))
            }

            val nextState = redoStack.removeAt(redoStack.lastIndex)
            replaceCurrentInputWith(nextState.text)
            updateKeyboardState {
                copy(
                    currentText = nextState.text,
                    cursorPosition = nextState.cursorPosition
                )
            }
            onCursorChangeListener?.invoke(nextState.cursorPosition)
        }
    }

    private fun updateCursorPosition(position: Int) {
        val currentText = _uiState.value.keyboardState.currentText
        val validPosition = position.coerceIn(0, currentText.length)
        updateKeyboardState { copy(cursorPosition = validPosition) }
    }

    private fun insertSuggestion(suggestion: String) {
        val currentState = _uiState.value.keyboardState
        val originalState = TextHistory(currentState.currentText, currentState.cursorPosition)
        addStateToUndoStack(originalState)

        val currentText = currentState.currentText

        println("current: $currentText")

        val trimmedText = currentText.trimEnd()

        val lastSpaceIndex = trimmedText.lastIndexOf(' ')

        val textWithoutLastWord = if (lastSpaceIndex != -1) {
            trimmedText.substring(0, lastSpaceIndex + 1)
        } else {
            ""
        }

        val finalText = "$textWithoutLastWord$suggestion "
        println("finalText: $finalText")

        replaceCurrentInputWith(finalText)

        updateKeyboardState {
            copy(
                currentText = finalText,
                cursorPosition = finalText.length
            )
        }
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

    private fun toggleQuickReply() {
        updateMode(KeyboardMode.QUICK_REPLY)
        onSelectQuickReplyActionChange(QuickReplyModule.POSITIVE)
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

    fun onSelectQuickReplyActionChange(action: QuickReplyModule) {
        _uiState.value = _uiState.value.copy(
            selectQuickReplyAction = action
        )
        viewModelScope.launch {
            when (val result =
                quickReplyUseCase(ServiceDataPass.lastMessage, uiState.value.language.name)) {
                is ResultWrapper.Success -> {
                    _uiState.value = _uiState.value.copy(
                        quickReplyList = result.data
                    )
                }
                is ResultWrapper.Failure -> {
                    Log.e("QuickReplyModule", "QuickReply failed: ${result.message}")
                }
                else -> {
                    Log.e("QuickReplyModule", "Unexpected Error")
                }
            }
        }
    }

    fun changeQuickReplyMessageType(action: QuickReplyModule) {
        _uiState.value = _uiState.value.copy(
            selectQuickReplyAction = action
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

    private fun startVoiceRecognition() {
        val context = KeyboardBridge.ime ?: return

        // Check if audio permission is granted
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Send broadcast to request permission
            val intent = Intent("com.ai.keyboard.REQUEST_AUDIO_PERMISSION")
            context.sendBroadcast(intent)

            _uiState.update {
                it.copy(voiceRecognitionError = "Requesting audio permission...")
            }
            return
        }

        // Check if speech recognition is available
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _uiState.update {
                it.copy(voiceRecognitionError = "Speech recognition not available")
            }
            return
        }

        // Initialize speech recognizer
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _uiState.update {
                    it.copy(
                        isVoiceRecording = true,
                        voiceRecognitionError = null
                    )
                }
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _uiState.update {
                    it.copy(isVoiceRecording = false)
                }
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

                _uiState.update {
                    it.copy(
                        isVoiceRecording = false,
                        voiceRecognitionError = errorMessage
                    )
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val recognizedText = matches?.firstOrNull() ?: ""

                if (recognizedText.isNotEmpty()) {
                    // Insert recognized text at cursor position
                    val currentState = _uiState.value.keyboardState
                    val newText = StringBuilder(currentState.currentText)
                        .insert(currentState.cursorPosition, recognizedText)
                        .toString()

                    updateKeyboardState {
                        copy(
                            currentText = newText,
                            cursorPosition = currentState.cursorPosition + recognizedText.length
                        )
                    }

                    // Notify text change
                    onTextChangeListener?.invoke(newText, currentState.cursorPosition + recognizedText.length)
                }

                _uiState.update {
                    it.copy(
                        isVoiceRecording = false,
                        voiceRecognitionText = recognizedText,
                        voiceRecognitionError = null
                    )
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partialText = matches?.firstOrNull() ?: ""

                _uiState.update {
                    it.copy(voiceRecognitionText = partialText)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer?.setRecognitionListener(recognitionListener)

        // Create recognition intent
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        // Start listening
        speechRecognizer?.startListening(intent)
    }

    fun stopVoiceRecognition() {
        speechRecognizer?.stopListening()
        _uiState.update {
            it.copy(isVoiceRecording = false)
        }
    }

    private fun pasteFromClipboard() {
        val clipboardText = clipboardManager.getClipboardText()
        if (clipboardText.isNotEmpty()) {
            val currentState = _uiState.value.keyboardState
            val currentText = currentState.currentText
            val cursorPosition = currentState.cursorPosition

            val newText = StringBuilder(currentText).apply {
                insert(cursorPosition, clipboardText)
            }.toString()

            val newCursorPosition = cursorPosition + clipboardText.length

            updateKeyboardState {
                copy(
                    currentText = newText,
                    cursorPosition = newCursorPosition
                )
            }

            onTextChangeListener?.invoke(newText, newCursorPosition)
            onCursorChangeListener?.invoke(newCursorPosition)
            onKeyPressListener?.invoke()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }


    fun toggleClipboardOpen() {
        updateMode(KeyboardMode.CLIP_BOARD)
    }


    private fun observeClipboard() {
        viewModelScope.launch {
            combine(
                getClipboardItemsUseCase(),
                manageClipboardUseCase.isEnabled()
            ) { items, isEnabled ->
                _uiState.update { currentState ->
                    currentState.copy(
                        clipboardState = ClipboardState(
                            items = items,
                            isEnabled = isEnabled
                        )
                    )
                }
            }.collect()
        }
    }

    fun addClipboardItem(text: String) {
        val isEnabled = _uiState.value.clipboardState.isEnabled
        if (isEnabled) {
            manageClipboardUseCase.addItem(text)
        }
    }

    private fun toggleClipboardEnabled() {
        viewModelScope.launch {
            val currentEnabled = _uiState.value.clipboardState.isEnabled
            manageClipboardUseCase.setEnabled(!currentEnabled)
        }
    }

    private fun toggleClipboardEditMode() {
        _uiState.update { state ->
            state.copy(
                clipboardState = state.clipboardState.copy(
                    isEditMode = !state.clipboardState.isEditMode,
                    selectedItems = emptySet() // Clear selection when toggling edit mode
                )
            )
        }
    }

    private fun selectClipboardItem(item: ClipboardItem) {
        // Insert the clipboard item text into the current text
        val currentText = _uiState.value.keyboardState.currentText
        val newText = currentText + item.text
        updateText(newText)
        onKeyPressListener?.invoke()

        // Add the selected text to clipboard for future use
        viewModelScope.launch {
            manageClipboardUseCase.addItem(item.text)
        }
    }

    private fun toggleClipboardItemSelection(item: ClipboardItem) {
        _uiState.update { state ->
            val currentSelected = state.clipboardState.selectedItems
            val newSelected = if (currentSelected.contains(item.id)) {
                currentSelected - item.id
            } else {
                currentSelected + item.id
            }

            state.copy(
                clipboardState = state.clipboardState.copy(
                    selectedItems = newSelected
                )
            )
        }
    }

    private fun deleteSelectedClipboardItems() {
        viewModelScope.launch {
            val selectedIds = _uiState.value.clipboardState.selectedItems.toList()
            if (selectedIds.isNotEmpty()) {
                manageClipboardUseCase.deleteItems(selectedIds)

                // Clear selection and exit edit mode
                _uiState.update { state ->
                    state.copy(
                        clipboardState = state.clipboardState.copy(
                            isEditMode = false,
                            selectedItems = emptySet()
                        )
                    )
                }
            }
        }
    }

    private fun updateText(text: String) {
        _uiState.update { currentState ->
            currentState.copy(
                keyboardState = currentState.keyboardState.copy(currentText = text)
            )
        }
        onTextChangeListener?.invoke(text, _uiState.value.keyboardState.cursorPosition)
        textChangeChannel.trySend(text)
    }
}



