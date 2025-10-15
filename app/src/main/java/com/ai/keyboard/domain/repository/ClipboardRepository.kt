package com.ai.keyboard.domain.repository

import com.ai.keyboard.domain.model.ClipboardItem
import kotlinx.coroutines.flow.Flow

interface ClipboardRepository {
    fun getClipboardItems(): Flow<List<ClipboardItem>>
    fun addClipboardItem(text: String)
    fun deleteClipboardItems(ids: List<String>)
    fun clearClipboard()
    suspend fun setClipboardEnabled(enabled: Boolean)
    fun isClipboardEnabled(): Flow<Boolean>
}