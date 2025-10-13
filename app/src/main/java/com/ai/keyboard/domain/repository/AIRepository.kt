package com.ai.keyboard.domain.repository

import com.ai.keyboard.domain.model.Suggestion

interface AIRepository {
    suspend fun getSuggestions(
        text: String,
        context: String,
        language: String
    ): Result<List<Suggestion>>

    suspend fun correctGrammar(text: String): Result<String>

    suspend fun predictNextWord(
        text: String,
        count: Int = 3
    ): Result<List<String>>

    suspend fun getSmartReplies(message: String): Result<List<String>>
}