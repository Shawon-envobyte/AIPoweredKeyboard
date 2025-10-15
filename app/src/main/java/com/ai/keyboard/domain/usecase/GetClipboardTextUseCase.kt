package com.ai.keyboard.domain.usecase

import com.ai.keyboard.core.clipboard.AndroidClipboardManager

class GetClipboardTextUseCase(
    private val clipboardManager: AndroidClipboardManager
) {
    operator fun invoke(): String {
        return clipboardManager.getClipboardText()
    }
}