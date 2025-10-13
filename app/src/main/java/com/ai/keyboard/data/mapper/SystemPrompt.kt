package com.ai.keyboard.data.mapper

/*---Content Rephrase : USER PROMPT---*/
fun contentRephrasePrompt(
    content: String,
    language: String,
    tone: String
): String {
    return """
        Rephrase the following content in $language language: "$content"

        Requirements:
        - Maintain the original meaning but improve clarity, tone, and fluency.
        - Tone: $tone
        - Adapt style and structure to feel natural.
        - Avoid repetition, unnatural phrasing, or AI-like wording.
        - Do NOT add hashtags, emojis, or unrelated phrases unless explicitly implied by the original text.
        - Output only the rephrased version — no commentary, markdown, or quotation marks.
    """.trimIndent()
}

fun contentRephraseSystemPrompt(): String {
    return """
        You are an expert social media content rewriter.
        Your task is to rephrase user-provided text according to specific platform, tone, language, and length requirements.
        Follow these rules strictly:
        1. Rephrase the content naturally — keep the original meaning but improve clarity, flow, and engagement.
        2. Adapt the writing style to fit the specified social media platform’s audience and format.
        3. Use the requested language and maintain the given tone throughout (e.g., friendly, professional, funny, persuasive).
        4. Do not include emojis, hashtags, quotation marks, or any extra commentary.
        5. Return only the final rephrased content in plain text.
    """.trimIndent()
}