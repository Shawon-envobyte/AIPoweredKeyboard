package com.ai.keyboard.presentation.screen.gif_keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.domain.model.GIFCategory
import com.ai.keyboard.domain.usecase.GetGIFCategoriesUseCase
import com.ai.keyboard.domain.usecase.GetTrendingGifsUseCase
import com.ai.keyboard.domain.usecase.SearchGifsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GifKeyboardViewModel(
    private val getGIFCategoriesUseCase: GetGIFCategoriesUseCase,
    private val getTrendingGifsUseCase: GetTrendingGifsUseCase,
    private val searchGifsUseCase: SearchGifsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GifKeyboardUIState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchCategories()
        browseGifs()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getGIFCategoriesUseCase()) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isLoading = false, categories = result.data) }
                }

                is ResultWrapper.Failure -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }

                is ResultWrapper.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun browseGifs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getTrendingGifsUseCase(20)) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isLoading = false, gifs = result.data) }
                }

                is ResultWrapper.Failure -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }

                is ResultWrapper.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun searchGifs(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            browseGifs()
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = searchGifsUseCase(query, 20)) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isLoading = false, gifs = result.data) }
                }

                is ResultWrapper.Failure -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }

                is ResultWrapper.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun onCategorySelected(category: GIFCategory) {
        searchGifs(category.searchTerm)
    }

    fun toggleSearchBar() {
        _uiState.update { it.copy(isSearchBarVisible = !it.isSearchBarVisible) }
    }
}
