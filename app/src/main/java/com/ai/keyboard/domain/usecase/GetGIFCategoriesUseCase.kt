package com.ai.keyboard.domain.usecase

import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.domain.model.GIFCategory
import com.ai.keyboard.domain.repository.GIFRepository

class GetGIFCategoriesUseCase(private val gifRepository: GIFRepository) {
    suspend operator fun invoke(): ResultWrapper<List<GIFCategory>> {
        return gifRepository.getCategories()
    }
}
