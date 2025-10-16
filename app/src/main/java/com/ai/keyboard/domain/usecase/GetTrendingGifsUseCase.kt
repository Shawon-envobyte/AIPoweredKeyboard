package com.ai.keyboard.domain.usecase

import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.domain.model.GIF
import com.ai.keyboard.domain.repository.GIFRepository

class GetTrendingGifsUseCase(private val gifRepository: GIFRepository) {
    suspend operator fun invoke(limit: Int): ResultWrapper<List<GIF>> {
        return gifRepository.browseGIFs(limit)
    }
}
