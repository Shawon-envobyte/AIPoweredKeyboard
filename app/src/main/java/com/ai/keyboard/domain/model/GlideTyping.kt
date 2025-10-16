package com.ai.keyboard.domain.model

import androidx.compose.ui.geometry.Offset

/**
 * Represents a point in the gesture path during glide typing
 */
data class GesturePoint(
    val position: Offset,
    val timestamp: Long,
    val key: String? = null // The key that this point corresponds to
)

/**
 * Represents a complete gesture path for glide typing
 */
data class GesturePath(
    val points: List<GesturePoint>,
    val startTime: Long,
    val endTime: Long
) {
    val duration: Long get() = endTime - startTime
    val length: Float get() = calculatePathLength()
    
    private fun calculatePathLength(): Float {
        if (points.size < 2) return 0f
        
        var totalLength = 0f
        for (i in 1 until points.size) {
            val prev = points[i - 1].position
            val curr = points[i].position
            totalLength += kotlin.math.sqrt(
                (curr.x - prev.x) * (curr.x - prev.x) + 
                (curr.y - prev.y) * (curr.y - prev.y)
            )
        }
        return totalLength
    }
}

/**
 * Represents a word prediction result from gesture analysis
 */
data class GestureWordPrediction(
    val word: String,
    val confidence: Float,
    val keySequence: List<String>
)

/**
 * Configuration for glide typing behavior
 */
data class GlideTypingConfig(
    val isEnabled: Boolean = true,
    val minGestureLength: Float = 50f,
    val minGestureDuration: Long = 100L,
    val maxPredictions: Int = 5,
    val confidenceThreshold: Float = 0.3f,
    val pathSmoothingFactor: Float = 0.8f
)

/**
 * Represents the current state of glide typing
 */
data class GlideTypingState(
    val isActive: Boolean = false,
    val gesturePath: List<GesturePoint> = emptyList(),
    val predictions: List<String> = emptyList(),
    val highlightedKeys: Set<String> = emptySet(), // Keys currently highlighted by gesture
    val currentKey: String? = null, // Currently active key under gesture
    val config: GlideTypingConfig = GlideTypingConfig()
)