package com.ai.keyboard.presentation.screen.gif_keyboard

import com.ai.keyboard.domain.model.GIF
import com.ai.keyboard.domain.model.GIFCategory

data class GifKeyboardUIState(
    val categories: List<GIFCategory> = emptyList(),
    val gifs: List<GIF> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val isSearchBarVisible: Boolean = false
)
