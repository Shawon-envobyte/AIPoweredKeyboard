package com.ai.keyboard.data.mapper


import com.ai.keyboard.domain.model.QuickReply
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

fun List<QuickReply>.toAPIRequestQuickReply(systemInstruction: String): APIRequest {
    return APIRequest(
        contents = listOf(
            Content(
                parts = listOf(
                    Part(
                        text = this.joinToString("\n") { it.toString() }
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
