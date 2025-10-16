package com.ai.keyboard.data.mapper

import com.ai.keyboard.data.model.TenorCategory
import com.ai.keyboard.data.model.TenorResult
import com.ai.keyboard.domain.model.GIFCategory
import com.ai.keyboard.domain.model.GIF

fun TenorResult.toGIF(): GIF {
    val gifUrl = media_formats?.get("gif")?.url ?: ""
    val previewUrl = media_formats?.get("nanogif")?.url ?: ""
    return GIF(
        id = id,
        url = gifUrl,
        previewUrl = previewUrl
    )
}

fun TenorCategory.toCategory(): GIFCategory {
    return GIFCategory(
        name = name,
        searchTerm = searchterm
    )
}
