package com.ai.keyboard.domain.usecase

import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.domain.repository.AIRepository

class GetAiWritingAssistanceUseCase(
    private val repository: AIRepository
) {
    suspend operator fun invoke(
        content: String,
        language: String,
        action: String
    ): ResultWrapper<String> {
        if (content.isBlank()) {
            return ResultWrapper.Success(content) // Return original text if empty
        }

        return repository.aiWritingAssistance(
            content = content,
            language = language,
            action = action
        )
    }
}