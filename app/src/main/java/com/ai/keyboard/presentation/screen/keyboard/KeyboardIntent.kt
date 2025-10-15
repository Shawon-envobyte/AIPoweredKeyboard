package com.ai.keyboard.presentation.screen.keyboard

import com.ai.keyboard.domain.model.ClipboardItem
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
    object VoiceToTextPressed : KeyboardIntent()
    object PasteFromClipboard : KeyboardIntent()

    // Clipboard intents
    object ClipboardOpen : KeyboardIntent()
    object ToggleClipboardEnabled : KeyboardIntent()
    object ToggleClipboardEditMode : KeyboardIntent()
    data class ClipboardItemSelected(val item: ClipboardItem) : KeyboardIntent()
    data class ClipboardItemToggleSelect(val item: ClipboardItem) : KeyboardIntent()
    object DeleteSelectedClipboardItems : KeyboardIntent()
}