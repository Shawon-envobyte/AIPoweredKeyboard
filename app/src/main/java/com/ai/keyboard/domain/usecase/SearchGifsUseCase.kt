package com.ai.keyboard.domain.usecase

import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.domain.model.GIF
import com.ai.keyboard.domain.repository.GIFRepository

class SearchGifsUseCase(private val gifRepository: GIFRepository) {
    suspend operator fun invoke(query: String, limit: Int): ResultWrapper<List<GIF>> {
        return gifRepository.searchGIFs(query, limit)
    }
}
