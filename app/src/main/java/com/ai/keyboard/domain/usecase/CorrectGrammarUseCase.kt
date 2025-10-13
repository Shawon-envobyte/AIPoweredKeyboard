package com.ai.keyboard.domain.usecase

import com.ai.keyboard.domain.repository.AIRepository

class CorrectGrammarUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(text: String): Result<String> {
        if (text.isEmpty()) return Result.success(text)
        return aiRepository.correctGrammar(text)
    }
}