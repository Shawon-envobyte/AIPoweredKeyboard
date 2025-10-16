package com.ai.keyboard.core.util

import android.content.Context
import android.util.Log
import androidx.compose.ui.geometry.Rect
import com.ai.keyboard.domain.model.GesturePath
import com.ai.keyboard.domain.model.GestureWordPrediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.exp

/**
 * Word prediction engine for glide typing gestures
 */
class WordPredictor(private val context: Context) {
    
    private var dictionary: Set<String> = emptySet()
    private var keyPositions: Map<String, Rect> = emptyMap()
    
    /**
     * Initialize the word predictor with dictionary and key positions
     */
    suspend fun initialize(keyPositions: Map<String, Rect>) {
        this.keyPositions = keyPositions
        loadDictionary()
    }
    
    /**
     * Predicts words based on the gesture path
     */
    suspend fun predictWords(
        gesturePath: GesturePath,
        maxPredictions: Int = 5,
        confidenceThreshold: Float = 0.3f
    ): List<GestureWordPrediction> = withContext(Dispatchers.Default) {
        
        if (gesturePath.points.isEmpty()) return@withContext emptyList()
        
        // Extract key sequence from gesture path
        val keySequence = extractKeySequence(gesturePath)
        if (keySequence.isEmpty()) return@withContext emptyList()
        
        // Find matching words in dictionary
        val candidates = findCandidateWords(keySequence)
        
        // Score and rank candidates
        val predictions = candidates.map { word ->
            val confidence = calculateConfidence(word, keySequence, gesturePath)
            GestureWordPrediction(
                word = word,
                confidence = confidence,
                keySequence = keySequence
            )
        }.filter { it.confidence >= confidenceThreshold }
            .sortedByDescending { it.confidence }
            .take(maxPredictions)
        
        predictions
    }
    
    /**
     * Extracts the sequence of keys from the gesture path
     */
    private fun extractKeySequence(gesturePath: GesturePath): List<String> {
        val keySequence = mutableListOf<String>()
        var lastKey: String? = null
        
        gesturePath.points.forEach { point ->
            val nearestKey = findNearestKey(point.position, keyPositions)
            
            // Only add key if it's different from the last one (avoid duplicates)
            if (nearestKey != null && nearestKey != lastKey) {
                keySequence.add(nearestKey)
                lastKey = nearestKey
            }
        }
        
        return keySequence
    }
    
    /**
     * Finds candidate words that could match the key sequence
     */
    private fun findCandidateWords(keySequence: List<String>): List<String> {
        if (keySequence.isEmpty()) return emptyList()
        
        return dictionary.filter { word ->
            isWordCandidate(word, keySequence)
        }
    }
    
    /**
     * Checks if a word could be a candidate for the given key sequence with Gboard-like flexibility
     */
    private fun isWordCandidate(word: String, keySequence: List<String>): Boolean {
        if (word.length < keySequence.size - 2 || word.length > keySequence.size + 3) {
            return false // Increased flexibility range
        }
        
        // Enhanced matching algorithm like Gboard
        var keyIndex = 0
        var matchedChars = 0
        
        for (char in word.lowercase()) {
            var found = false
            
            // Look for exact match first
            for (i in keyIndex until keySequence.size) {
                if (keySequence[i].lowercase() == char.toString()) {
                    keyIndex = i + 1
                    matchedChars++
                    found = true
                    break
                }
            }
            
            if (!found) {
                // Look for nearby keys (Gboard's fuzzy matching)
                val nearbyKeys = getNearbyKeys(char.toString())
                for (i in keyIndex until keySequence.size) {
                    if (keySequence[i].lowercase() in nearbyKeys) {
                        keyIndex = i + 1
                        matchedChars++
                        found = true
                        break
                    }
                }
            }
            
            // Allow skipping some characters for better flexibility
            if (!found && matchedChars > word.length * 0.6f) {
                // Skip this character if we've matched enough of the word
                continue
            }
            
            if (!found) return false
        }
        
        // Require at least 70% character match for candidates
        return matchedChars >= (word.length * 0.7f).toInt()
    }
    
    /**
     * Calculates confidence score for a word prediction with Gboard-like accuracy
     */
    private fun calculateConfidence(
        word: String,
        keySequence: List<String>,
        gesturePath: GesturePath
    ): Float {
        var confidence = 0f
        
        // Enhanced base confidence from key sequence match (increased weight)
        val sequenceMatch = calculateSequenceMatch(word, keySequence)
        confidence += sequenceMatch * 0.5f // Increased from 0.4f
        
        // Path smoothness factor (Gboard considers gesture fluidity)
        val pathSmoothness = calculatePathSmoothness(gesturePath)
        confidence += pathSmoothness * 0.15f // Reduced from 0.2f
        
        // Word frequency score (Gboard heavily weights common words)
        val frequencyScore = getWordFrequencyScore(word)
        confidence += frequencyScore * 0.25f // Increased weight for frequency
        
        // Length penalty (prefer words closer to gesture length)
        val lengthPenalty = calculateLengthPenalty(word, keySequence)
        confidence -= lengthPenalty * 0.1f
        
        // Gesture velocity consistency (new Gboard-like feature)
        val velocityConsistency = calculateVelocityConsistency(gesturePath)
        confidence += velocityConsistency * 0.1f
        
        return confidence.coerceIn(0f, 1f)
    }
        
    /**
     * Calculates velocity consistency for Gboard-like gesture analysis
     */
    private fun calculateVelocityConsistency(gesturePath: GesturePath): Float {
        if (gesturePath.points.size < 3) return 0.5f
        
        val velocities = mutableListOf<Float>()
        
        // Calculate velocities between consecutive points
        for (i in 1 until gesturePath.points.size) {
            val prev = gesturePath.points[i - 1]
            val curr = gesturePath.points[i]
            
            val distance = kotlin.math.sqrt(
                (curr.position.x - prev.position.x) * (curr.position.x - prev.position.x) +
                (curr.position.y - prev.position.y) * (curr.position.y - prev.position.y)
            )
            
            val timeDiff = curr.timestamp - prev.timestamp
            val velocity = if (timeDiff > 0) distance / timeDiff else 0f
            velocities.add(velocity)
        }
        
        if (velocities.isEmpty()) return 0.5f
        
        // Calculate velocity variance (lower variance = more consistent = higher score)
        val avgVelocity = velocities.average().toFloat()
        val variance = velocities.map { (it - avgVelocity) * (it - avgVelocity) }.average().toFloat()
        
        // Convert variance to consistency score (0-1, where 1 is most consistent)
        val maxVariance = 1000f // Reasonable maximum variance threshold
        return (1f - (variance / maxVariance).coerceIn(0f, 1f))
    }
    
    /**
     * Calculates how well the word matches the key sequence
     */
    private fun calculateSequenceMatch(word: String, keySequence: List<String>): Float {
        if (keySequence.isEmpty()) return 0f
        
        var matches = 0
        var keyIndex = 0
        
        for (char in word.lowercase()) {
            if (keyIndex < keySequence.size && 
                keySequence[keyIndex].lowercase() == char.toString()) {
                matches++
                keyIndex++
            }
        }
        
        return matches.toFloat() / word.length
    }
    
    /**
     * Calculates path smoothness score
     */
    private fun calculatePathSmoothness(gesturePath: GesturePath): Float {
        if (gesturePath.points.size < 3) return 0.5f
        
        var totalAngleChange = 0f
        var angleCount = 0
        
        for (i in 1 until gesturePath.points.size - 1) {
            val p1 = gesturePath.points[i - 1].position
            val p2 = gesturePath.points[i].position
            val p3 = gesturePath.points[i + 1].position
            
            val angle1 = kotlin.math.atan2(p2.y - p1.y, p2.x - p1.x)
            val angle2 = kotlin.math.atan2(p3.y - p2.y, p3.x - p2.x)
            
            var angleDiff = kotlin.math.abs(angle2 - angle1)
            if (angleDiff > kotlin.math.PI) {
                angleDiff = 2 * kotlin.math.PI.toFloat() - angleDiff
            }
            
            totalAngleChange += angleDiff
            angleCount++
        }
        
        val avgAngleChange = if (angleCount > 0) totalAngleChange / angleCount else 0f
        return exp(-avgAngleChange).toFloat()
    }
    
    /**
     * Gets word frequency score (simplified - in real implementation, use actual frequency data)
     */
    private fun getWordFrequencyScore(word: String): Float {
        // Common English words get higher scores
        val commonWords = setOf(
            "the", "and", "you", "that", "was", "for", "are", "with", "his", "they",
            "this", "have", "from", "not", "word", "but", "what", "some", "time",
            "very", "when", "come", "here", "just", "like", "long", "make", "many",
            "over", "such", "take", "than", "them", "well", "were"
        )
        
        return when {
            word.lowercase() in commonWords -> 0.8f
            word.length in 3..7 -> 0.6f
            else -> 0.4f
        }
    }
    
    /**
     * Calculates length penalty
     */
    private fun calculateLengthPenalty(word: String, keySequence: List<String>): Float {
        val lengthDiff = kotlin.math.abs(word.length - keySequence.size)
        return when {
            lengthDiff == 0 -> 1f
            lengthDiff == 1 -> 0.8f
            lengthDiff == 2 -> 0.6f
            else -> 0.3f
        }
    }
    
    /**
     * Gets nearby keys for a character (for typo tolerance)
     */
    private fun getNearbyKeys(char: String): Set<String> {
        val qwertyLayout = mapOf(
            "q" to setOf("w", "a"),
            "w" to setOf("q", "e", "a", "s"),
            "e" to setOf("w", "r", "s", "d"),
            "r" to setOf("e", "t", "d", "f"),
            "t" to setOf("r", "y", "f", "g"),
            "y" to setOf("t", "u", "g", "h"),
            "u" to setOf("y", "i", "h", "j"),
            "i" to setOf("u", "o", "j", "k"),
            "o" to setOf("i", "p", "k", "l"),
            "p" to setOf("o", "l"),
            "a" to setOf("q", "w", "s", "z"),
            "s" to setOf("a", "w", "e", "d", "z", "x"),
            "d" to setOf("s", "e", "r", "f", "x", "c"),
            "f" to setOf("d", "r", "t", "g", "c", "v"),
            "g" to setOf("f", "t", "y", "h", "v", "b"),
            "h" to setOf("g", "y", "u", "j", "b", "n"),
            "j" to setOf("h", "u", "i", "k", "n", "m"),
            "k" to setOf("j", "i", "o", "l", "m"),
            "l" to setOf("k", "o", "p"),
            "z" to setOf("a", "s", "x"),
            "x" to setOf("z", "s", "d", "c"),
            "c" to setOf("x", "d", "f", "v"),
            "v" to setOf("c", "f", "g", "b"),
            "b" to setOf("v", "g", "h", "n"),
            "n" to setOf("b", "h", "j", "m"),
            "m" to setOf("n", "j", "k")
        )
        
        return qwertyLayout[char.lowercase()] ?: emptySet()
    }
    
    /**
     * Finds the nearest key to a position
     */
    private fun findNearestKey(position: androidx.compose.ui.geometry.Offset, keyPositions: Map<String, Rect>): String? {
        var nearestKey: String? = null
        var minDistance = Float.MAX_VALUE
        
        keyPositions.forEach { (key, rect) ->
            if (rect.contains(position)) {
                return key
            }
            
            val keyCenter = androidx.compose.ui.geometry.Offset(rect.center.x, rect.center.y)
            val distance = kotlin.math.sqrt(
                (position.x - keyCenter.x) * (position.x - keyCenter.x) +
                (position.y - keyCenter.y) * (position.y - keyCenter.y)
            )
            
            if (distance < minDistance) {
                minDistance = distance
                nearestKey = key
            }
        }
        
        return nearestKey
    }
    
    /**
     * Loads dictionary from assets
     */
    private suspend fun loadDictionary() = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.assets.open("en_words.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            dictionary = reader.useLines { lines ->
                lines.map { line ->
                    // Remove quotes and commas from the dictionary file format
                    line.trim()
                        .removePrefix("\"")
                        .removeSuffix("\",")
                        .removeSuffix("\"")
                        .lowercase()
                }
                .filter { it.isNotEmpty() && it.length >= 2 }
                .toSet()
            }
            
            Log.d("WordPredictor", "Dictionary loaded with ${dictionary.size} words")
        } catch (e: Exception) {
            Log.e("WordPredictor", "Failed to load dictionary", e)
            // Fallback to basic dictionary if file not found
            dictionary = getBasicDictionary()
        }
    }
    
    /**
     * Fallback basic dictionary
     */
    private fun getBasicDictionary(): Set<String> {
        return setOf(
            "the", "and", "you", "that", "was", "for", "are", "with", "his", "they",
            "this", "have", "from", "not", "word", "but", "what", "some", "time",
            "very", "when", "come", "here", "just", "like", "long", "make", "many",
            "over", "such", "take", "than", "them", "well", "were", "been", "has",
            "had", "who", "oil", "sit", "now", "find", "down", "day", "did", "get",
            "may", "him", "old", "see", "two", "way", "could", "people", "my", "than",
            "first", "water", "been", "call", "who", "its", "now", "find", "long",
            "down", "day", "did", "get", "has", "him", "his", "how", "man", "new",
            "now", "old", "see", "two", "way", "who", "boy", "did", "its", "let",
            "put", "say", "she", "too", "use"
        )
    }
}