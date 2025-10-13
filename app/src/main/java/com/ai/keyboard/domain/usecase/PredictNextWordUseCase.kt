package com.ai.keyboard.domain.usecase

import com.ai.keyboard.domain.repository.AIRepository

class PredictNextWordUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(
        text: String,
        count: Int = 3
    ): Result<List<String>> {
        if (text.isEmpty()) return Result.success(emptyList())
        return aiRepository.predictNextWord(text, count)
    }
}