package com.ai.keyboard.data.source.local

import com.ai.keyboard.domain.model.Suggestion
import com.ai.keyboard.domain.model.SuggestionType

class LocalAIEngine {
    val commonWords = listOf(
        "the", "is", "at", "which", "on", "and", "a", "an", "as", "are", "was", "were", "been", "be",
        "have", "has", "had", "do", "does", "did", "will", "would", "should", "could", "may", "might",
        "must", "can", "hello", "hi", "how", "what", "where", "when", "why", "who", "thank", "thanks",
        "please", "sorry", "yes", "no", "okay", "ok", "good", "great", "awesome", "nice", "cool", "love",
        "like", "today", "tomorrow", "yesterday", "now", "later", "soon", "never", "always", "often",
        "sometimes", "usually", "rarely", "really", "very", "too", "enough", "more", "less", "much",
        "many", "few", "some", "any", "each", "every", "either", "neither", "both", "all", "none",
        "most", "other", "another", "first", "second", "third", "next", "last", "new", "old", "young",
        "big", "small", "large", "tiny", "huge", "long", "short", "tall", "high", "low", "early", "late",
        "fast", "slow", "quick", "beautiful", "pretty", "handsome", "ugly", "smart", "clever", "funny",
        "serious", "happy", "sad", "angry", "upset", "tired", "sleepy", "bored", "excited", "afraid",
        "scared", "brave", "kind", "mean", "nice", "friendly", "polite", "rude", "lazy", "hardworking",
        "rich", "poor", "clean", "dirty", "hot", "cold", "warm", "cool", "wet", "dry", "hungry", "thirsty",
        "full", "empty", "right", "left", "up", "down", "in", "out", "over", "under", "above", "below",
        "before", "after", "between", "inside", "outside", "near", "far", "here", "there", "everywhere",
        "home", "house", "room", "kitchen", "bathroom", "bedroom", "garden", "school", "college",
        "university", "office", "store", "shop", "market", "mall", "restaurant", "hotel", "airport",
        "station", "bus", "train", "car", "bike", "bicycle", "truck", "plane", "boat", "ship", "taxi",
        "road", "street", "lane", "avenue", "bridge", "river", "mountain", "hill", "forest", "tree",
        "flower", "grass", "sky", "cloud", "rain", "sun", "moon", "star", "wind", "storm", "snow",
        "weather", "season", "spring", "summer", "autumn", "winter", "day", "night", "morning", "evening",
        "noon", "midnight", "time", "hour", "minute", "second", "clock", "watch", "calendar", "today",
        "tomorrow", "yesterday", "week", "month", "year", "decade", "century", "life", "death", "birth",
        "baby", "child", "boy", "girl", "man", "woman", "father", "mother", "brother", "sister", "friend",
        "teacher", "student", "doctor", "nurse", "engineer", "driver", "police", "soldier", "lawyer",
        "farmer", "worker", "artist", "musician", "singer", "actor", "player", "chef", "waiter",
        "scientist", "programmer", "developer", "designer", "writer", "author", "painter", "dancer",
        "photographer", "journalist", "leader", "manager", "boss", "team", "group", "family", "parents",
        "children", "friends", "people", "person", "someone", "everyone", "nobody", "anyone", "somebody",
        "thing", "everything", "nothing", "something", "place", "country", "city", "town", "village",
        "area", "world", "earth", "land", "sea", "ocean", "water", "fire", "air", "wind", "light", "dark",
        "color", "red", "blue", "green", "yellow", "black", "white", "pink", "orange", "purple", "brown",
        "gray", "gold", "silver", "happy", "sad", "angry", "tired", "hungry", "beautiful", "awesome",
        "wonderful", "amazing", "perfect", "nice", "cool", "funny", "crazy", "silly", "interesting",
        "boring", "important", "difficult", "easy", "simple", "complicated", "possible", "impossible",
        "true", "false", "real", "fake", "same", "different", "right", "wrong", "correct", "incorrect",
        "best", "better", "worse", "bad", "good", "great", "amazing", "awesome", "fantastic", "terrible",
        "horrible", "awful", "delicious", "tasty", "fresh", "sweet", "bitter", "sour", "salty", "spicy",
        "food", "drink", "coffee", "tea", "milk", "juice", "water", "bread", "rice", "meat", "fish",
        "egg", "fruit", "apple", "banana", "mango", "orange", "grape", "strawberry", "pineapple",
        "vegetable", "carrot", "potato", "tomato", "onion", "garlic", "pepper", "salad", "soup",
        "breakfast", "lunch", "dinner", "snack", "dessert", "cake", "cookie", "chocolate", "ice", "cream",
        "money", "cash", "price", "cost", "cheap", "expensive", "free", "buy", "sell", "shop", "market",
        "work", "job", "office", "meeting", "project", "task", "goal", "plan", "success", "fail", "win",
        "lose", "learn", "study", "teach", "read", "write", "speak", "listen", "talk", "say", "tell",
        "hear", "see", "look", "watch", "show", "think", "know", "understand", "remember", "forget",
        "believe", "trust", "feel", "want", "need", "try", "start", "begin", "stop", "end", "go", "come",
        "walk", "run", "jump", "sit", "stand", "sleep", "wake", "eat", "drink", "play", "work", "drive",
        "fly", "swim", "live", "die", "grow", "build", "make", "create", "open", "close", "move", "stay",
        "wait", "help", "call", "send", "receive", "use", "find", "give", "take", "put", "get", "keep",
        "change", "turn", "show", "watch", "ask", "answer", "follow", "lead", "meet", "love", "hate",
        "laugh", "cry", "smile", "thank", "forgive", "hope", "wish", "dream", "pray", "care", "share",
        "plan", "decide", "choose", "prefer", "like", "dislike", "agree", "disagree", "help", "support",
        "build", "fix", "break", "repair", "wash", "clean", "cook", "drive", "walk", "run", "fly", "travel",
        "visit", "stay", "leave", "arrive", "return", "go", "come", "move", "send", "receive", "write",
        "read", "learn", "teach", "study", "listen", "speak", "hear", "see", "think", "know", "understand",
        "remember", "believe", "trust", "try", "start", "stop", "begin", "finish", "end", "open", "close",
        "turn", "wait", "sit", "stand", "sleep", "wake", "eat", "drink", "laugh", "cry", "smile", "hope",
        "pray", "dream", "love", "hate", "care", "help", "support", "build", "fix", "break", "repair",
        "plan", "decide", "choose", "prefer", "like", "dislike", "agree", "disagree", "think", "know",
        "understand", "remember", "forget", "believe", "trust", "try", "start", "stop", "finish"
    )

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
    private val commonWordsSet = commonWords.toSet()
    private val sortedCommonWords = commonWords.sorted() // For binary search

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