package com.ai.keyboard.data.repository

import com.ai.keyboard.data.source.local.datastore.PreferencesDataStore
import com.ai.keyboard.domain.model.ClipboardItem
import com.ai.keyboard.domain.repository.ClipboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ClipboardRepositoryImpl(
    private val preferencesDataStore: PreferencesDataStore
) : ClipboardRepository {

    private val _clipboardItems = MutableStateFlow<List<ClipboardItem>>(emptyList())

    override fun getClipboardItems(): Flow<List<ClipboardItem>> {
        return _clipboardItems.asStateFlow()
    }

    override fun addClipboardItem(text: String) {
        val currentItems = _clipboardItems.value.toMutableList()

        // Remove duplicate if exists
        currentItems.removeAll { it.text == text }

        // Add new item at the beginning
        val newItem = ClipboardItem(
            id = UUID.randomUUID().toString(),
            text = text,
            timestamp = System.currentTimeMillis()
        )
        currentItems.add(0, newItem)

        // Keep only last 20 items
        if (currentItems.size > 20) {
            currentItems.removeAt(currentItems.size - 1)
        }

        _clipboardItems.value = currentItems
    }

    override fun deleteClipboardItems(ids: List<String>) {
        val currentItems = _clipboardItems.value.toMutableList()
        currentItems.removeAll { it.id in ids }
        _clipboardItems.value = currentItems
    }

    override fun clearClipboard() {
        _clipboardItems.value = emptyList()
    }

    override suspend fun setClipboardEnabled(enabled: Boolean) {
        preferencesDataStore.saveClipboardEnabled(enabled)
    }

    override fun isClipboardEnabled(): Flow<Boolean> {
        return preferencesDataStore.isClipboardEnabled()
    }
}