package com.ai.keyboard.core.di.data

import com.ai.keyboard.core.network.HttpClientProvider
import com.ai.keyboard.data.source.remote.api.APIDataSource
import com.ai.keyboard.data.source.remote.api.APIDataSourceImpl
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientProvider.client }
    single<APIDataSource> { APIDataSourceImpl(get()) }
}