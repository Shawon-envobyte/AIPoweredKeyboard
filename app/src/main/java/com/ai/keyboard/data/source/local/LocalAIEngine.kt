package com.ai.keyboard.data.source.local

import com.ai.keyboard.domain.model.Suggestion
import com.ai.keyboard.domain.model.SuggestionType

class LocalAIEngine {
    private val commonWords = listOf(
        "the", "is", "at", "which", "on", "and", "a", "an",
        "as", "are", "was", "were", "been", "be", "have", "has",
        "had", "do", "does", "did", "will", "would", "should",
        "could", "may", "might", "must", "can", "hello", "hi",
        "how", "what", "where", "when", "why", "who", "thank",
        "thanks", "please", "sorry", "yes", "no", "okay", "ok",
        "good", "great", "awesome", "nice", "cool", "love", "like"
    )

    private val nextWordPredictions = mapOf(
        "i" to listOf("am", "will", "have", "can", "would"),
        "you" to listOf("are", "can", "will", "should", "have"),
        "hello" to listOf("there", "world", "everyone", "friend"),
        "how" to listOf("are", "is", "do", "can", "about"),
        "thank" to listOf("you", "goodness", "god"),
        "good" to listOf("morning", "evening", "afternoon", "night", "day"),
        "have" to listOf("a", "been", "to", "not", "you")
    )

    fun getSuggestions(
        text: String,
        context: String,
        language: String
    ): List<Suggestion> {
        if (text.isEmpty()) return emptyList()

        val lastWord = text.trim().split(" ").lastOrNull()?.lowercase() ?: return emptyList()

        return commonWords
            .filter { it.startsWith(lastWord, ignoreCase = true) && it != lastWord }
            .take(5)
            .mapIndexed { index, word ->
                Suggestion(
                    text = word,
                    confidence = 1.0f - (index * 0.1f),
                    type = SuggestionType.AUTOCOMPLETE
                )
            }
    }

    fun predictNextWord(text: String, count: Int): List<String> {
        val words = text.trim().lowercase().split(" ")
        val lastWord = words.lastOrNull() ?: return emptyList()

        return nextWordPredictions[lastWord]?.take(count)
            ?: commonWords.take(count)
    }

    fun correctGrammar(text: String): String {
        // Simple grammar corrections
        var corrected = text
        val corrections = mapOf(
            "teh" to "the",
            "adn" to "and",
            "yuor" to "your",
            "recieve" to "receive",
            "occured" to "occurred"
        )

        corrections.forEach { (wrong, right) ->
            corrected = corrected.replace(Regex("\\b$wrong\\b", RegexOption.IGNORE_CASE), right)
        }

        return corrected
    }

    fun getSmartReplies(message: String): List<String> {
        val lowerMessage = message.lowercase()

        return when {
            lowerMessage.contains("thank") -> listOf("You're welcome!", "No problem!", "Anytime!")
            lowerMessage.contains("how are") -> listOf(
                "I'm good, thanks!",
                "Doing well!",
                "Great, you?"
            )

            lowerMessage.contains("?") -> listOf("Yes", "No", "Maybe")
            else -> listOf("Thanks!", "Sounds good", "OK")
        }
    }
}