package com.ai.keyboard.core.di.presentation

import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        KeyboardViewModel(get(), get(), get(), get(), get(), get(), get(), get())
    }
}