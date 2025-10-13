package com.ai.keyboard.data.repository

import com.ai.keyboard.data.source.local.datastore.PreferencesDataStore
import com.ai.keyboard.domain.model.KeyboardTheme
import com.ai.keyboard.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val preferencesDataStore: PreferencesDataStore
) : SettingsRepository {

    override suspend fun saveTheme(theme: KeyboardTheme) {
        preferencesDataStore.saveTheme(theme)
    }

    override fun getTheme(): Flow<KeyboardTheme> {
        return preferencesDataStore.getTheme()
    }

    override suspend fun saveHapticEnabled(enabled: Boolean) {
        preferencesDataStore.saveHapticEnabled(enabled)
    }

    override fun isHapticEnabled(): Flow<Boolean> {
        return preferencesDataStore.isHapticEnabled()
    }

    override suspend fun saveSoundEnabled(enabled: Boolean) {
        preferencesDataStore.saveSoundEnabled(enabled)
    }

    override fun isSoundEnabled(): Flow<Boolean> {
        return preferencesDataStore.isSoundEnabled()
    }
}