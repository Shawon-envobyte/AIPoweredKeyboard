package com.ai.keyboard.domain.usecase

import com.ai.keyboard.domain.model.Suggestion
import com.ai.keyboard.domain.repository.AIRepository

class GetSuggestionsUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(
        text: String,
        context: String = "",
        language: String = "en"
    ): Result<List<Suggestion>> {
        if (text.isEmpty()) return Result.success(emptyList())
        return aiRepository.getSuggestions(text, context, language)
    }
}