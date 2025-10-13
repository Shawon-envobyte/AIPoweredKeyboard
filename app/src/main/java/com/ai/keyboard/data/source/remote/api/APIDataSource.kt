package com.ai.keyboard.data.source.remote.api

import com.hashtag.generator.ai.post.writer.data.model.APIRequest
import com.hashtag.generator.ai.post.writer.data.model.APIResponse

interface APIDataSource {

    suspend fun getResponseFromAPI(
        request: APIRequest
    ): APIResponse
}