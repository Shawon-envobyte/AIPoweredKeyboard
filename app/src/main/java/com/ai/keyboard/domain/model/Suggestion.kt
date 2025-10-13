package com.ai.keyboard.domain.model

data class Suggestion(
    val text: String,
    val confidence: Float,
    val type: SuggestionType
)