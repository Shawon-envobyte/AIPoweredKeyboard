package com.ai.keyboard.presentation.model

import androidx.annotation.DrawableRes
import com.ai.keyboard.R

enum class ActionButtonType(
    val id: String,
    val label: String,
    val emoji: String? = null,
    @DrawableRes val icon: Int? = null,
    val isGradient: Boolean = false
) {
    REPHRASE(
        id = "rephrase",
        label = "Rephrase",
        icon = R.drawable.ic_magic
    ),
    GRAMMAR_FIX(
        id = "grammar_fix",
        label = "Grammar Fix",
        emoji = "🛠️"
    ),
    ADD_EMOJI(
        id = "add emoji",
        label = "Add emoji",
        emoji = "🤗"
    ),

}