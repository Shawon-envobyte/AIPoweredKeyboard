package com.ai.keyboard.core.di.domain

import com.ai.keyboard.domain.usecase.CorrectGrammarUseCase
import com.ai.keyboard.domain.usecase.FixGrammarUseCase
import com.ai.keyboard.domain.usecase.GetAiWritingAssistanceUseCase
import com.ai.keyboard.domain.usecase.GetClipboardTextUseCase
import com.ai.keyboard.domain.usecase.GetSuggestionsUseCase
import com.ai.keyboard.domain.usecase.GetTranslateUseCase
import com.ai.keyboard.domain.usecase.GetWordToneUseCase
import com.ai.keyboard.domain.usecase.PredictNextWordUseCase
import com.ai.keyboard.domain.usecase.QuickReplyUseCase
import com.ai.keyboard.domain.usecase.RephraseContentUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetSuggestionsUseCase(get()) }
    factory { PredictNextWordUseCase(get()) }
    factory { CorrectGrammarUseCase(get()) }
    single { RephraseContentUseCase(get()) }
    factory { FixGrammarUseCase(get()) }
    factory { QuickReplyUseCase(get()) }
    factory { GetWordToneUseCase(get()) }
    factory { GetAiWritingAssistanceUseCase(get()) }
    factory { GetTranslateUseCase(get()) }
    factory { GetClipboardTextUseCase(get()) }

}