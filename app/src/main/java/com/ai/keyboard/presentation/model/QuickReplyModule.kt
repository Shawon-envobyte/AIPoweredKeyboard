package com.ai.keyboard.presentation.model

import androidx.annotation.DrawableRes
import com.ai.keyboard.R

enum class QuickReplyModule(
    val id: String,
    val label: String,
    val emoji: String? = null,
    @DrawableRes val icon: Int? = null,
    val isGradient: Boolean = false
) {
    POSITIVE(
        id = "positive",
        label = "Positive",
        icon = R.drawable.ic_positive,
    ),
    NEGATIVE(
        id = "negative",
        label = "Negative",
        icon = R.drawable.ic_negative,
    ),
    NEUTRAL(
        id = "neutral",
        label = "Neutral",
        icon = R.drawable.ic_neutral,
    )

}