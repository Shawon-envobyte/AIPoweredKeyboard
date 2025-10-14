package com.ai.keyboard.presentation.model

import androidx.annotation.DrawableRes
import com.ai.keyboard.R

enum class WordToneType(
    val id: String,
    val label: String,
    val emoji: String? = null,
    @DrawableRes val icon: Int? = null,
    val isGradient: Boolean = false
) {
    REWRITE(
        id = "rewrite",
        label = "ReWrite",
        icon = R.drawable.ic_rewrite,
    ),
    PROFESSIONAL(
        id = "professional",
        label = "Professional",
        icon = R.drawable.ic_professional,
    ),
    CASUAL(
        id = "casual",
        label = "Casual",
        icon = R.drawable.ic_casual,
    ),

}