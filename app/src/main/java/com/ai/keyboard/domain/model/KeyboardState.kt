package com.ai.keyboard.domain.model

data class KeyboardState(
    val currentText: String = "",
    val cursorPosition: Int = 0,
    val mode: KeyboardMode = KeyboardMode.LOWERCASE,
    val suggestions: List<Suggestion> = emptyList(),
    val isLoading: Boolean = false,
    val selectedLanguage: String = "en",
    val theme: KeyboardTheme = KeyboardTheme.Light,
    val isHapticEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true
)