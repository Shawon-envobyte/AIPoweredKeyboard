package com.ai.keyboard.data.mapper


import com.hashtag.generator.ai.post.writer.data.model.APIRequest
import com.hashtag.generator.ai.post.writer.data.model.Content
import com.hashtag.generator.ai.post.writer.data.model.Part

fun String.toAPIRequest(systemInstruction: String? = null): APIRequest {
    return APIRequest(
        contents = listOf(
            Content(
                parts = listOf(
                    Part(
                        text = this
                    )
                )
            )
        ),
        systemInstruction = Content(
            parts = listOf(
                Part(
                    text = systemInstruction
                )
            )
        )
    )
}
