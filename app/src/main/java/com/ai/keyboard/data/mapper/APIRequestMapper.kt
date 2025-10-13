package com.ai.keyboard.data.mapper


import com.hashtag.generator.ai.post.writer.data.model.APIRequest
import com.hashtag.generator.ai.post.writer.data.model.Content
import com.hashtag.generator.ai.post.writer.data.model.InlineData
import com.hashtag.generator.ai.post.writer.data.model.Part
import io.ktor.util.encodeBase64

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
        systemInstruction =
            Content(
                parts = listOf(
                    Part(
                        text = systemInstruction
                    )
                )

            )
    )
}

fun String.toAPIImageRequest(
    imageBytes: ByteArray,
    systemInstruction: String? = null,
    mimeType: String
): APIRequest {
    return APIRequest(
        contents = listOf(
            Content(
                parts = listOf(
                    Part(
                        text = this
                    ),
                    Part(
                        inlineData = InlineData(
                            mimeType = mimeType,
                            data = imageBytes.encodeBase64()
                        )
                    )
                )
            )
        ),
        systemInstruction =
            Content(
                parts = listOf(
                    Part(
                        text = systemInstruction
                    )
                )

            )
    )
}
