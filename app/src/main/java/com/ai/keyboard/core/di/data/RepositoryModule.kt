package com.ai.keyboard.core.di.data

import com.ai.keyboard.data.repository.AIRepositoryImpl
import com.ai.keyboard.data.repository.ClipboardRepositoryImpl
import com.ai.keyboard.data.repository.SettingsRepositoryImpl
import com.ai.keyboard.domain.repository.AIRepository
import com.ai.keyboard.domain.repository.ClipboardRepository
import com.ai.keyboard.domain.repository.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AIRepository> { AIRepositoryImpl(localAI = get(), cache = get(), get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(preferencesDataStore = get()) }
    single<ClipboardRepository> { ClipboardRepositoryImpl(preferencesDataStore = get()) }
}