package com.ai.keyboard.data.source.remote.api

import com.ai.keyboard.data.model.APIRequest
import com.ai.keyboard.data.model.APIResponse

interface APIDataSource {

    suspend fun getResponseFromAPI(
        request: APIRequest
    ): APIResponse
}