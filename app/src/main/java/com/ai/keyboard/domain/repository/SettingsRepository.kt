package com.ai.keyboard.domain.repository

import com.ai.keyboard.domain.model.KeyboardTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveTheme(theme: KeyboardTheme)
    fun getTheme(): Flow<KeyboardTheme>

    suspend fun saveHapticEnabled(enabled: Boolean)
    fun isHapticEnabled(): Flow<Boolean>

    suspend fun saveSoundEnabled(enabled: Boolean)
    fun isSoundEnabled(): Flow<Boolean>
}