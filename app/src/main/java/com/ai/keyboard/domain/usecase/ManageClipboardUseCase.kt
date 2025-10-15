package com.ai.keyboard.domain.usecase

import com.ai.keyboard.domain.repository.ClipboardRepository
import kotlinx.coroutines.flow.Flow

class ManageClipboardUseCase(
    private val clipboardRepository: ClipboardRepository
) {
    fun addItem(text: String) {
        clipboardRepository.addClipboardItem(text)
    }

    fun deleteItems(ids: List<String>) {
        clipboardRepository.deleteClipboardItems(ids)
    }

    fun clearAll() {
        clipboardRepository.clearClipboard()
    }

    suspend fun setEnabled(enabled: Boolean) {
        clipboardRepository.setClipboardEnabled(enabled)
    }

    fun isEnabled(): Flow<Boolean> {
        return clipboardRepository.isClipboardEnabled()
    }
}