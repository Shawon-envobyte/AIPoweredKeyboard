package com.ai.keyboard.core.di.presentation

import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        KeyboardViewModel(
            getSuggestionsUseCase = get(),
            predictNextWordUseCase = get(),
            correctGrammarUseCase = get(),
            settingsRepository = get()
        )
    }
}