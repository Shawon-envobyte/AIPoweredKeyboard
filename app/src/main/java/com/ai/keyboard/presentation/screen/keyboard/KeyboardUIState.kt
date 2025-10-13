package com.ai.keyboard.presentation.screen.keyboard

import com.ai.keyboard.domain.model.KeyboardState
import com.ai.keyboard.domain.model.Suggestion

data class KeyboardUIState(
    val keyboardState: KeyboardState = KeyboardState(),
    val suggestions: List<Suggestion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)