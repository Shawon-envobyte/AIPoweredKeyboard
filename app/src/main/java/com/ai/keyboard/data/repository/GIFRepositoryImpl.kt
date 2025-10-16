package com.ai.keyboard.data.repository

import com.ai.keyboard.BuildConfig
import com.ai.keyboard.core.util.ResultWrapper
import com.ai.keyboard.data.mapper.toCategory
import com.ai.keyboard.data.mapper.toGIF
import com.ai.keyboard.data.model.TenorCategoriesResponse
import com.ai.keyboard.data.model.TenorResponse
import com.ai.keyboard.domain.model.GIFCategory
import com.ai.keyboard.domain.model.GIF
import com.ai.keyboard.domain.repository.GIFRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class GIFRepositoryImpl(
    private val httpClient: HttpClient,
) : GIFRepository {
    override suspend fun searchGIFs(
        query: String,
        limit: Int
    ): ResultWrapper<List<GIF>> {
        return try {
            val response = httpClient.get("https://tenor.googleapis.com/v2/search") {
                parameter("q", query)
                parameter("key", BuildConfig.TENOR_API_KEY)
                parameter("limit", limit)
            }.body<TenorResponse>()
            val gifs = response.results.map { it.toGIF() }
            ResultWrapper.Success(gifs)
        } catch (e: Exception) {
            ResultWrapper.Failure(e.message ?: "An error occurred")
        }
    }

    override suspend fun browseGIFs(limit: Int): ResultWrapper<List<GIF>> {
        return try {
            val response = httpClient.get("https://tenor.googleapis.com/v2/featured") {
                parameter("key", BuildConfig.TENOR_API_KEY)
                parameter("limit", limit)
            }.body<TenorResponse>()
            val gifs = response.results.map { it.toGIF() }
            ResultWrapper.Success(gifs)
        } catch (e: Exception) {
            ResultWrapper.Failure(e.message ?: "An error occurred")
        }
    }

    override suspend fun getCategories(): ResultWrapper<List<GIFCategory>> {
        return try {
            val response = httpClient.get("https://tenor.googleapis.com/v2/categories") {
                parameter("key", BuildConfig.TENOR_API_KEY)
            }.body<TenorCategoriesResponse>()
            val categories = response.tags.map { it.toCategory() }
            ResultWrapper.Success(categories)
        } catch (e: Exception) {
            ResultWrapper.Failure(e.message ?: "An error occurred")
        }
    }
}
