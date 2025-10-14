package com.ai.keyboard.presentation.model

import androidx.annotation.DrawableRes
import com.ai.keyboard.R

enum class AIWritingAssistanceType(
    val id: String,
    val label: String,
    val emoji: String? = null,
    @DrawableRes val icon: Int? = null,
    val isGradient: Boolean = false
) {
    CHATGPT(
        id = "chatgpt",
        label = "ChatGpt",
        icon = R.drawable.ic_gpt,
    ),
    HUMANISE(
        id = "humanise",
        label = "Humanise",
        icon = R.drawable.ic_person,
    ),
    REPLY(
        id = "reply",
        label = "Reply",
        icon = R.drawable.ic_reply,
    ),

}