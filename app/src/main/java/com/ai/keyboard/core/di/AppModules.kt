package com.ai.keyboard.core.di

import com.ai.keyboard.data.repository.AIRepositoryImpl
import com.ai.keyboard.data.repository.SettingsRepositoryImpl
import com.ai.keyboard.data.source.local.CacheManager
import com.ai.keyboard.data.source.local.LocalAIEngine
import com.ai.keyboard.data.source.local.datastore.PreferencesDataStore
import com.ai.keyboard.domain.repository.AIRepository
import com.ai.keyboard.domain.repository.SettingsRepository
import com.ai.keyboard.domain.usecase.CorrectGrammarUseCase
import com.ai.keyboard.domain.usecase.GetSuggestionsUseCase
import com.ai.keyboard.domain.usecase.PredictNextWordUseCase
import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val dataModule = module {
    // Local Data Sources
    single { LocalAIEngine() }
    single { CacheManager() }
    single { PreferencesDataStore(androidContext()) }

    // Repositories
    single<AIRepository> {
        AIRepositoryImpl(
            localAI = get(),
            cache = get()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            preferencesDataStore = get()
        )
    }
}

val domainModule = module {
    // Use Cases
    factory { GetSuggestionsUseCase(get()) }
    factory { PredictNextWordUseCase(get()) }
    factory { CorrectGrammarUseCase(get()) }
}

val presentationModule = module {
    // ViewModel
    viewModel {
        KeyboardViewModel(
            getSuggestionsUseCase = get(),
            predictNextWordUseCase = get(),
            correctGrammarUseCase = get(),
            settingsRepository = get()
        )
    }
}

val appModules = listOf(dataModule, domainModule, presentationModule)