package com.ai.keyboard.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TenorResponse(
    val results: List<TenorResult>
)

@Serializable
data class TenorResult(
    val id: String,
    val media_formats: Map<String, MediaObject>? = null
)

@Serializable
data class MediaObject(
    val url: String,
)

@Serializable
data class TenorCategoriesResponse(
    val tags: List<TenorCategory>
)

@Serializable
data class TenorCategory(
    val searchterm: String,
    val name: String
)
