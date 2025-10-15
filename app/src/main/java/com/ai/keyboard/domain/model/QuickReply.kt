package com.ai.keyboard.domain.model

data class QuickReply(
    val positive: List<String>,
    val neutral: List<String>,
    val negative: List<String>
)