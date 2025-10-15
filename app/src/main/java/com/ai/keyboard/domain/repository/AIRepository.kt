package com.ai.keyboard.domain.repository

import com.ai.keyboard.core.util.ResultWrapper
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

    suspend fun rephraseContent(
        content: String,
        language: String,
        tone: String
    ): ResultWrapper<String>

    suspend fun fixGrammar(
        content: String,
        language: String,
        action: String
    ): ResultWrapper<String>


    suspend fun wordTone(
        content: String,
        language: String,
        action: String
    ): ResultWrapper<String>

    suspend fun aiWritingAssistance(
        content: String,
        language: String,
        action: String
    ): ResultWrapper<String>

    suspend fun translate(
        content: String,
        language: String
    ): ResultWrapper<String>
}