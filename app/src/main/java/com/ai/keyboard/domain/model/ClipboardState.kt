package com.ai.keyboard.domain.model


data class ClipboardState(
    val items: List<ClipboardItem> = emptyList(),
    val isEnabled: Boolean = true,
    val isEditMode: Boolean = false,
    val selectedItems: Set<String> = emptySet()
)