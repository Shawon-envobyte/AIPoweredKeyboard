package com.ai.keyboard.domain.usecase

import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.domain.model.QuickReply
import com.ai.keyboard.domain.repository.AIRepository

class QuickReplyUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(text: String, language: String): ResultWrapper<List<QuickReply>> {
        if (text.isEmpty()) return ResultWrapper.Success(emptyList())
        return aiRepository.quickReply(text, language)
    }
}