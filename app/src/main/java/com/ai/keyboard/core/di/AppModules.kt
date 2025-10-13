package com.ai.keyboard.core.di

import com.ai.keyboard.core.di.data.localModule
import com.ai.keyboard.core.di.data.networkModule
import com.ai.keyboard.core.di.data.repositoryModule
import com.ai.keyboard.core.di.domain.useCaseModule
import com.ai.keyboard.core.di.presentation.viewModelModule

val appModules = listOf(
    localModule,
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)