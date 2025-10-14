package com.ai.keyboard.presentation.screen.keyboard

import com.ai.keyboard.domain.model.KeyboardState
import com.ai.keyboard.domain.model.Suggestion
import com.ai.keyboard.presentation.model.AIWritingAssistanceType
import com.ai.keyboard.presentation.model.ActionButtonType
import com.ai.keyboard.presentation.model.LanguageType
import com.ai.keyboard.presentation.model.WordToneType

data class KeyboardUIState(
    val keyboardState: KeyboardState = KeyboardState(),
    val suggestions: List<Suggestion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val correctedText: String = "",
    val language: LanguageType = LanguageType.ENGLISH,
    val selectedAction: ActionButtonType = ActionButtonType.REPHRASE,
    val selectedAiAction: AIWritingAssistanceType = AIWritingAssistanceType.CHATGPT,
    val selectedWordAction: WordToneType = WordToneType.REWRITE,
    val inputFieldText: String = ""
)