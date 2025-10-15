package com.ai.keyboard.presentation.screen.keyboard

import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardTheme

sealed class KeyboardIntent {
    data class KeyPressed(val action: KeyAction) : KeyboardIntent()
    data class SuggestionSelected(val suggestion: String) : KeyboardIntent()
    data class CursorPositionChanged(val position: Int) : KeyboardIntent()
    object ShiftPressed : KeyboardIntent()
    object SymbolPressed : KeyboardIntent()
    object EmojiPressed : KeyboardIntent()
    object AlphabetPressed : KeyboardIntent()
    object ExtendedSymbolPressed : KeyboardIntent()
    data class ThemeChanged(val theme: KeyboardTheme) : KeyboardIntent()
    object ToggleHaptic : KeyboardIntent()
    object ToggleSound : KeyboardIntent()
    object ToggleNumerRow : KeyboardIntent()
    object FixGrammarPressed : KeyboardIntent()
    object GetQuickReply : KeyboardIntent()
    object RewritePressed : KeyboardIntent()
    object AiAssistancePressed : KeyboardIntent()
    object TranslatePressed : KeyboardIntent()
}