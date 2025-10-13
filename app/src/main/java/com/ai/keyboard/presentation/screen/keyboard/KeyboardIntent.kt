package com.ai.keyboard.presentation.screen.keyboard

import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardTheme

sealed class KeyboardIntent {
    data class KeyPressed(val action: KeyAction) : KeyboardIntent()
    data class SuggestionSelected(val suggestion: String) : KeyboardIntent()
    data class CursorPositionChanged(val position: Int) : KeyboardIntent()
    object ShiftPressed : KeyboardIntent()
    object SymbolPressed : KeyboardIntent()
    data class ThemeChanged(val theme: KeyboardTheme) : KeyboardIntent()
    object ToggleHaptic : KeyboardIntent()
    object ToggleSound : KeyboardIntent()
}