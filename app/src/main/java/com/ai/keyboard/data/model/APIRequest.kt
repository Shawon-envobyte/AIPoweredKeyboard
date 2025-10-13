package com.hashtag.generator.ai.post.writer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class APIRequest(
    val systemInstruction: Content,
    val contents: List<Content>
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String
)