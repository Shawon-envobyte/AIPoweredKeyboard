package com.ai.keyboard.domain.usecase

import com.ai.keyboard.domain.model.ClipboardItem
import com.ai.keyboard.domain.repository.ClipboardRepository
import kotlinx.coroutines.flow.Flow

class GetClipboardItemsUseCase(
    private val clipboardRepository: ClipboardRepository
) {
    operator fun invoke(): Flow<List<ClipboardItem>> {
        return clipboardRepository.getClipboardItems()
    }
}