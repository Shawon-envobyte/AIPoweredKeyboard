package com.ai.keyboard.core.di.data

import com.ai.keyboard.data.source.local.CacheManager
import com.ai.keyboard.data.source.local.LocalAIEngine
import com.ai.keyboard.data.source.local.datastore.PreferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single { LocalAIEngine() }
    single { CacheManager() }
    single { PreferencesDataStore(androidContext()) }
}