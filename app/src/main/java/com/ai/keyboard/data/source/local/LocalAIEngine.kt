package com.ai.keyboard.data.source.local

import android.content.Context
import com.ai.keyboard.domain.model.Suggestion
import com.ai.keyboard.domain.model.SuggestionType
import java.io.BufferedReader
import java.io.InputStreamReader

class LocalAIEngine(private val context: Context) {
    val commonWords: List<String> by lazy {
        loadWordsFromAssets("en_words.txt")
    }

    private fun loadWordsFromAssets(fileName: String): List<String> {
        val words = mutableListOf<String>()
        try {
            context.assets.open(fileName).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.forEachLine { line ->
                        val word = line.trim().removeSuffix(",").removeSurrounding("\"")
                        if (word.isNotEmpty()) {
                            words.add(word)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return words
    }

    private val nextWordPredictions = mapOf(
        "i" to listOf("am", "will", "have", "can", "would"),
        "you" to listOf("are", "can", "will", "should", "have"),
        "hello" to listOf("there", "world", "everyone", "friend"),
        "how" to listOf("are", "is", "do", "can", "about"),
        "thank" to listOf("you", "goodness", "god"),
        "good" to listOf("morning", "evening", "afternoon", "night", "day"),
        "have" to listOf("a", "been", "to", "not", "you"),
        "it" to listOf("is", "was", "will", "has", "seems", "can", "would"),
        "we" to listOf("are", "can", "will", "should", "have", "need", "must"),
        "they" to listOf("are", "were", "will", "have", "can", "go"),
        "he" to listOf("is", "was", "has", "will", "can", "said"),
        "she" to listOf("is", "was", "has", "will", "can", "said"),
        "what" to listOf("is", "are", "do", "did", "if", "about"),
        "where" to listOf("is", "are", "do", "did", "can", "should"),
        "when" to listOf("is", "are", "do", "did", "will"),
        "why" to listOf("is", "are", "do", "did", "not"),
        "this" to listOf("is", "was", "will", "has", "looks", "sounds"),
        "that" to listOf("is", "was", "will", "has", "means", "sounds"),
        "the" to listOf("first", "last", "next", "only", "best", "worst", "most"),
        "a" to listOf("new", "good", "great", "big", "small", "long", "short"),
        "an" to listOf("apple", "orange", "hour", "interesting", "easy", "old"),
        "to" to listOf("be", "have", "do", "go", "get", "see", "make"),
        "in" to listOf("the", "a", "an", "my", "your", "our"),
        "for" to listOf("the", "a", "an", "me", "you", "us", "them"),
        "of" to listOf("the", "a", "an", "course", "my", "your"),
        "with" to listOf("the", "a", "an", "me", "you", "us"),
        "on" to listOf("the", "a", "an", "my", "your", "top"),
        "at" to listOf("the", "a", "an", "least", "first", "last"),
        "from" to listOf("the", "a", "an", "me", "you", "here"),
        "can" to listOf("i", "you", "we", "they", "be", "do"),
        "will" to listOf("i", "you", "we", "they", "be", "not"),
        "is" to listOf("a", "an", "the", "not", "it", "that", "this"),
        "are" to listOf("you", "we", "they", "the", "not"),
        "was" to listOf("a", "an", "the", "not", "i", "he", "she", "it"),
        "let's" to listOf("go", "see", "do", "start", "try", "make"),
        "my" to listOf("name", "friend", "favorite", "car", "house", "life"),
        "your" to listOf("name", "friend", "favorite", "car", "house", "life"),
        "there" to listOf("is", "are", "was", "were", "will", "can"),
        "here" to listOf("is", "are", "we", "you", "i"),
        "one" to listOf("of", "day", "more", "last", "time"),
        "about" to listOf("the", "a", "an", "it", "that", "this"),
        "if" to listOf("you", "i", "we", "they", "it", "that"),
        "so" to listOf("i", "you", "we", "they", "it", "that"),
        "but" to listOf("i", "you", "we", "they", "it", "that"),
        "all" to listOf("of", "the", "my", "your", "our"),
        "just" to listOf("a", "one", "like", "do", "go"),
        "out" to listOf("of", "there", "here", "with", "on"),
        "up" to listOf("to", "there", "on", "in", "next"),
        "go" to listOf("to", "back", "home", "out", "for"),
        "get" to listOf("the", "a", "an", "it", "ready", "to"),
        "make" to listOf("it", "a", "an", "sure", "sense"),
        "see" to listOf("you", "it", "the", "if", "what"),
        "know" to listOf("what", "how", "that", "if", "the"),
        "think" to listOf("i", "about", "so", "that", "it's"),
        "time" to listOf("to", "for", "is", "was", "flies")
    )

    // Pre-compute word sets for faster lookups
    private val commonWordsSet by lazy { commonWords.toSet() }
    private val sortedCommonWords by lazy { commonWords.sorted() } // For binary search

    fun getSuggestions(
        text: String,
        context: String,
        language: String
    ): List<Suggestion> {
        if (text.isEmpty()) return emptyList()

        val lastWord = text.trim().substringAfterLast(' ').lowercase()
        if (lastWord.isEmpty()) return emptyList()

        // Use binary search for better performance on large word lists
        val startIndex = sortedCommonWords.binarySearch { it.compareTo(lastWord) }
        val insertionPoint = if (startIndex < 0) -(startIndex + 1) else startIndex

        val suggestions = mutableListOf<Suggestion>()
        var index = insertionPoint
        
        // Collect matching words efficiently
        while (index < sortedCommonWords.size && suggestions.size < 5) {
            val word = sortedCommonWords[index]
            if (word.startsWith(lastWord, ignoreCase = true) && word != lastWord) {
                suggestions.add(
                    Suggestion(
                        text = word,
                        confidence = 1.0f - (suggestions.size * 0.1f),
                        type = SuggestionType.AUTOCOMPLETE
                    )
                )
            } else if (!word.startsWith(lastWord, ignoreCase = true)) {
                break // No more matches possible in sorted list
            }
            index++
        }

        return suggestions
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