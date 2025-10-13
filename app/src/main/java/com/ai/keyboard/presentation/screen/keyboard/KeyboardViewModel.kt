package com.ai.keyboard.presentation.screen.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.domain.model.KeyboardState
import com.ai.keyboard.domain.model.KeyboardTheme
import com.ai.keyboard.domain.repository.SettingsRepository
import com.ai.keyboard.domain.usecase.GetSuggestionsUseCase
import com.ai.keyboard.domain.usecase.RephraseContentUseCase
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
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(KeyboardUIState())
    val uiState: StateFlow<KeyboardUIState> = _uiState.asStateFlow()

    private val textChangeChannel = Channel<String>(Channel.CONFLATED)

    private var onTextChangeListener: ((String, Int) -> Unit)? = null
    private var onCursorChangeListener: ((Int) -> Unit)? = null
    private var onKeyPressListener: (() -> Unit)? = null

    init {
        observeSettings()
        setupSuggestionDebouncing()
    }

    fun rephraseContent() {
        viewModelScope.launch {
            val currentText = _uiState.value.keyboardState.currentText
            val result = rephraseContentUseCase(currentText, "Friendly", "English")
            if (result is ResultWrapper.Success) {

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
    }

    @OptIn(FlowPreview::class)
    private fun setupSuggestionDebouncing() {
        viewModelScope.launch {
            textChangeChannel.receiveAsFlow()
                .debounce(300)
                .distinctUntilChanged()
                .collect { text ->
                    fetchSuggestions(text)
                }
        }
    }

    fun setOnTextChangeListener(listener: (String, Int) -> Unit) {
        onTextChangeListener = listener
    }

    fun setOnCursorChangeListener(listener: (Int) -> Unit) {
        onCursorChangeListener = listener
    }

    fun setOnKeyPressListener(listener: () -> Unit) {
        onKeyPressListener = listener
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
        }
    }

    private fun handleKeyPress(action: KeyAction) {
        val currentState = _uiState.value.keyboardState
        var currentText = currentState.currentText
        var cursorPosition = currentState.cursorPosition
        var textChanged = true

        when (action) {
            is KeyAction.Character -> {
                val char = when (currentState.mode) {
                    KeyboardMode.UPPERCASE, KeyboardMode.CAPS_LOCK -> action.char.uppercase()
                    else -> action.char
                }
                currentText = currentText.replaceRange(cursorPosition, cursorPosition, char)
                cursorPosition += char.length
            }

            is KeyAction.Backspace -> {
                if (cursorPosition > 0 && currentText.isNotEmpty()) {
                    currentText = currentText.removeRange(cursorPosition - 1, cursorPosition)
                    cursorPosition--
                } else {
                    textChanged = false
                }
            }

            is KeyAction.Enter -> {
                val char = "\n"
                currentText = currentText.replaceRange(cursorPosition, cursorPosition, char)
                cursorPosition += char.length
            }

            is KeyAction.Space -> {
                val char = " "
                currentText = currentText.replaceRange(cursorPosition, cursorPosition, char)
                cursorPosition += char.length
            }

            is KeyAction.MoveCursor -> {
                val newPosition = cursorPosition + action.amount
                cursorPosition = newPosition.coerceIn(0, currentText.length)
                textChanged = false
            }

            is KeyAction.InsertSuggestion -> {
                currentText = currentText.replaceRange(cursorPosition, cursorPosition, action.text)
                cursorPosition += action.text.length
            }

            KeyAction.Shift, KeyAction.Symbol, KeyAction.ExtendedSymbol -> {
                textChanged = false
            }
        }

        updateKeyboardState { copy(currentText = currentText, cursorPosition = cursorPosition) }

        if (textChanged) {
            onTextChangeListener?.invoke(currentText, cursorPosition)
            textChangeChannel.trySend(currentText)
        } else {
            onCursorChangeListener?.invoke(cursorPosition)
        }

        onKeyPressListener?.invoke()

        if (currentState.mode == KeyboardMode.UPPERCASE && action is KeyAction.Character) {
            updateMode(KeyboardMode.LOWERCASE)
        }
    }

    private fun updateText(text: String) {
        updateKeyboardState {
            copy(
                currentText = text,
                cursorPosition = text.length
            )
        }
        onTextChangeListener?.invoke(text, text.length)
    }

    private fun updateCursorPosition(position: Int) {
        updateKeyboardState { copy(cursorPosition = position) }
    }

    private fun insertSuggestion(suggestion: String) {
        val currentText = _uiState.value.keyboardState.currentText
        val words = currentText.split(" ")
        val newText = if (words.isNotEmpty()) {
            words.dropLast(1).joinToString(" ") +
                    (if (words.size > 1) " " else "") + suggestion + " "
        } else {
            suggestion + " "
        }
        updateText(newText)
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

    private fun toggleSymbol() {
        val currentMode = _uiState.value.keyboardState.mode
        val newMode = if (currentMode == KeyboardMode.SYMBOLS) {
            KeyboardMode.LOWERCASE
        } else {
            KeyboardMode.SYMBOLS
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

    private fun updateMode(mode: KeyboardMode) {
        updateKeyboardState { copy(mode = mode) }
    }

    private fun fetchSuggestions(text: String) {
        viewModelScope.launch {
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
}