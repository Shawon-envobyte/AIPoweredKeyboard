package com.ai.keyboard.domain.repository

import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.domain.model.GIFCategory
import com.ai.keyboard.domain.model.GIF

interface GIFRepository {
    suspend fun searchGIFs(query: String, limit: Int): ResultWrapper<List<GIF>>
    suspend fun browseGIFs(limit: Int): ResultWrapper<List<GIF>>
    suspend fun getCategories(): ResultWrapper<List<GIFCategory>>
}
