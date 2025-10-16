package com.ai.keyboard.core.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.ai.keyboard.domain.model.GesturePoint
import com.ai.keyboard.domain.model.GesturePath
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Utility class for detecting and processing gesture paths in glide typing
 */
class GestureDetector {
    
    private val currentPoints = mutableListOf<GesturePoint>()
    private var gestureStartTime: Long = 0
    private var isGestureActive = false
    
    // Cache for distance calculations
    private var lastCalculatedDistance = 0f
    private var lastDistancePoints: Pair<Offset, Offset>? = null
    
    /**
     * Starts a new gesture tracking session
     */
    fun startGesture(startPoint: Offset): Boolean {
        currentPoints.clear()
        gestureStartTime = System.currentTimeMillis()
        isGestureActive = true
        
        addPoint(startPoint)
        return true
    }
    
    /**
     * Adds a point to the current gesture path with Gboard-like processing
     */
    /**
     * Adds a point to the current gesture with enhanced filtering and temporal smoothing
     */
    fun addPoint(point: Offset, key: String? = null): Boolean {
        if (!isGestureActive) return false
        
        val currentTime = System.currentTimeMillis()
        
        // Check for gesture timeout (2 seconds max)
        if (currentTime - gestureStartTime > MAX_GESTURE_DURATION) {
            android.util.Log.d("GestureDetector", "Gesture timeout after ${MAX_GESTURE_DURATION}ms")
            return false // This will cause the gesture to be cancelled
        }
        
        // Enhanced point filtering - similar to Gboard
        if (currentPoints.isNotEmpty()) {
            val lastPoint = currentPoints.last()
            val distance = calculateDistance(point, lastPoint.position)
            
            // Skip points that are too close together (reduces noise)
            if (distance < MIN_POINT_DISTANCE) {
                return false
            }
            
            // Skip points with excessive velocity (likely noise or accidental input)
            val timeDelta = currentTime - lastPoint.timestamp
            if (timeDelta > 0) {
                val velocity = distance / timeDelta
                if (velocity > MAX_VELOCITY_THRESHOLD) {
                    return false
                }
            }
        }
        
        // Create gesture point with temporal smoothing
        val gesturePoint = if (currentPoints.size >= SMOOTHING_WINDOW_SIZE) {
            val smoothingFactor = 0.7f // Adjust for desired smoothness
            val recentPoints = currentPoints.takeLast(SMOOTHING_WINDOW_SIZE - 1)
            val avgX = recentPoints.map { it.position.x }.average().toFloat()
            val avgY = recentPoints.map { it.position.y }.average().toFloat()
            
            val smoothedX = point.x * smoothingFactor + avgX * (1 - smoothingFactor)
            val smoothedY = point.y * smoothingFactor + avgY * (1 - smoothingFactor)
            
            GesturePoint(Offset(smoothedX, smoothedY), currentTime, key)
        } else {
            GesturePoint(point, currentTime, key)
        }
        
        currentPoints.add(gesturePoint)
        
        // Limit the number of points to prevent memory issues
        if (currentPoints.size > 200) {
            currentPoints.removeAt(0)
        }
        
        return true
    }
    
    /**
     * Ends the current gesture and returns the complete path
     */
    fun endGesture(): GesturePath? {
        if (!isGestureActive || currentPoints.isEmpty()) {
            reset()
            return null
        }
        
        val endTime = System.currentTimeMillis()
        val path = GesturePath(
            points = currentPoints.toList(),
            startTime = gestureStartTime,
            endTime = endTime
        )
        
        reset()
        return if (isValidGesture(path)) path else null
    }
    
    /**
     * Cancels the current gesture
     */
    fun cancelGesture() {
        reset()
    }
    
    /**
     * Gets the current gesture path (for real-time display)
     */
    fun getCurrentPath(): List<GesturePoint> = currentPoints.toList()
    
    /**
     * Checks if the current gesture has timed out
     */
    fun hasTimedOut(): Boolean {
        if (!isGestureActive) return false
        return System.currentTimeMillis() - gestureStartTime > MAX_GESTURE_DURATION
    }
    
    /**
     * Gets the remaining time for the current gesture in milliseconds
     */
    fun getRemainingTime(): Long {
        if (!isGestureActive) return 0L
        val elapsed = System.currentTimeMillis() - gestureStartTime
        return maxOf(0L, MAX_GESTURE_DURATION - elapsed)
    }
    
    /**
     * Checks if a gesture is currently active
     */
    fun isActive(): Boolean = isGestureActive
    
    /**
     * Smooths the gesture path to reduce noise
     */
    fun smoothPath(points: List<GesturePoint>, factor: Float = 0.8f): List<GesturePoint> {
        if (points.size < 3) return points
        
        val smoothedPoints = mutableListOf<GesturePoint>()
        smoothedPoints.add(points.first()) // Keep first point unchanged
        
        for (i in 1 until points.size - 1) {
            val prev = points[i - 1].position
            val curr = points[i].position
            val next = points[i + 1].position
            
            // Apply smoothing using weighted average
            val smoothedX = curr.x * factor + (prev.x + next.x) * (1 - factor) / 2
            val smoothedY = curr.y * factor + (prev.y + next.y) * (1 - factor) / 2
            
            smoothedPoints.add(
                points[i].copy(
                    position = Offset(smoothedX, smoothedY)
                )
            )
        }
        
        smoothedPoints.add(points.last()) // Keep last point unchanged
        return smoothedPoints
    }
    
    /**
     * Finds the key that a point is closest to
     */
    fun findNearestKey(point: Offset, keyPositions: Map<String, Rect>): String? {
        var nearestKey: String? = null
        var minDistance = Float.MAX_VALUE
        
        keyPositions.forEach { (key, rect) ->
            val keyCenter = Offset(rect.center.x, rect.center.y)
            val distance = calculateDistance(point, keyCenter)
            
            if (distance < minDistance && rect.contains(point)) {
                minDistance = distance
                nearestKey = key
            }
        }
        
        // Log detailed key detection information
        if (nearestKey != null) {
            val keyRect = keyPositions[nearestKey]
            android.util.Log.d("GestureDetector", 
                "Key '$nearestKey' detected at (${point.x.toInt()}, ${point.y.toInt()}) " +
                "within bounds [${keyRect?.left?.toInt()}, ${keyRect?.top?.toInt()}, " +
                "${keyRect?.right?.toInt()}, ${keyRect?.bottom?.toInt()}] distance: ${minDistance.toInt()}")
        } else {
            android.util.Log.d("GestureDetector", 
                "No key found at position (${point.x.toInt()}, ${point.y.toInt()})")
        }
        
        return nearestKey
    }
    
    /**
     * Calculates the velocity at a given point in the gesture
     */
    fun calculateVelocity(pointIndex: Int, windowSize: Int = 3): Float {
        if (pointIndex < windowSize || pointIndex >= currentPoints.size - windowSize) {
            return 0f
        }
        
        val startPoint = currentPoints[pointIndex - windowSize]
        val endPoint = currentPoints[pointIndex + windowSize]
        
        val distance = calculateDistance(startPoint.position, endPoint.position)
        val timeDiff = endPoint.timestamp - startPoint.timestamp
        
        return if (timeDiff > 0) distance / timeDiff else 0f
    }
    
    private fun reset() {
        currentPoints.clear()
        gestureStartTime = 0
        isGestureActive = false
        lastDistancePoints = null
    }
    
    private fun isValidGesture(path: GesturePath): Boolean {
        return path.points.size >= MIN_GESTURE_POINTS &&
                path.duration >= MIN_GESTURE_DURATION &&
                path.length >= MIN_GESTURE_LENGTH
    }
    
    private fun calculateDistance(point1: Offset, point2: Offset): Float {
        // Use cached result if calculating the same distance
        val pointPair = Pair(point1, point2)
        if (lastDistancePoints == pointPair) {
            return lastCalculatedDistance
        }
        
        val dx = point1.x - point2.x
        val dy = point1.y - point2.y
        val distance = sqrt(dx * dx + dy * dy)
        
        // Cache the result
        lastCalculatedDistance = distance
        lastDistancePoints = pointPair
        
        return distance
    }
    
    companion object {
        // Gboard-like sensitivity settings
        private const val MIN_POINT_DISTANCE = 3f // Reduced for better sensitivity
        private const val MIN_GESTURE_POINTS = 2 // Reduced minimum points
        private const val MIN_GESTURE_DURATION = 50L // Reduced for faster response
        private const val MIN_GESTURE_LENGTH = 30f // Reduced for shorter gestures
        private const val MAX_VELOCITY_THRESHOLD = 2000f // Maximum velocity for valid gestures
        private const val SMOOTHING_WINDOW_SIZE = 3 // Window size for smoothing
        private const val MAX_GESTURE_DURATION = 2000L // Maximum gesture duration (2 seconds)
    }
}