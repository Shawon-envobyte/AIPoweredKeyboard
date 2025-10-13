package com.ai.keyboard.data.source.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ai.keyboard.domain.model.KeyboardTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "keyboard_settings")

class PreferencesDataStore(private val context: Context) {

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
        private val HAPTIC_KEY = booleanPreferencesKey("haptic_enabled")
        private val SOUND_KEY = booleanPreferencesKey("sound_enabled")
        private val NUMER_ROW_ENABLED_KEY = booleanPreferencesKey("number_row_enabled")
    }

    suspend fun saveTheme(theme: KeyboardTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    fun getTheme(): Flow<KeyboardTheme> {
        return context.dataStore.data.map { preferences ->
            val themeName = preferences[THEME_KEY] ?: KeyboardTheme.Light.name
            KeyboardTheme.valueOf(themeName)
        }
    }

    suspend fun saveHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAPTIC_KEY] = enabled
        }
    }

    fun isHapticEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[HAPTIC_KEY] ?: true
        }
    }

    suspend fun saveSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_KEY] = enabled
        }
    }

    fun isSoundEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[SOUND_KEY] ?: true
        }
    }

    suspend fun saveNumberRowEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NUMER_ROW_ENABLED_KEY] = enabled
        }
    }

    fun isNumberRowEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[NUMER_ROW_ENABLED_KEY] ?: false
        }
    }
}