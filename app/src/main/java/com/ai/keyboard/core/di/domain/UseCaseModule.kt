package com.ai.keyboard.core.di.domain

import com.ai.keyboard.domain.usecase.CorrectGrammarUseCase
import com.ai.keyboard.domain.usecase.GetSuggestionsUseCase
import com.ai.keyboard.domain.usecase.PredictNextWordUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetSuggestionsUseCase(get()) }
    factory { PredictNextWordUseCase(get()) }
    factory { CorrectGrammarUseCase(get()) }
}