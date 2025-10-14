package com.ai.keyboard.domain.model

sealed class KeyAction {
    data class Character(val char: String) : KeyAction()
    object Backspace : KeyAction()
    object Enter : KeyAction()
    object Space : KeyAction()
    object Shift : KeyAction()
    object Symbol : KeyAction()
    object ExtendedSymbol : KeyAction()
    data class MoveCursor(val amount: Int) : KeyAction()
    data class SelectAndDelete(val amount: Int) : KeyAction()
    data class InsertSuggestion(val text: String) : KeyAction()
}