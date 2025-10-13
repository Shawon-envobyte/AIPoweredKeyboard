package com.ai.keyboard.domain.usecase

import com.ai.keyboard.domain.repository.AIRepository

class RephraseContentUseCase(
    private val repository: AIRepository
) {
    suspend operator fun invoke(
        content: String,
        tone: String,
        language: String
    ) = repository.rephraseContent(
        content = content,
        tone = tone,
        language = language
    )
}