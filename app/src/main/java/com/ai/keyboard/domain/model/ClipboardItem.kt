package com.ai.keyboard.domain.model

data class ClipboardItem(
    val id: String,
    val text: String,
    val timestamp: Long,
    val isSelected: Boolean = false
)