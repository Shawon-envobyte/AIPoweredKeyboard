package com.ai.keyboard.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ai.keyboard.domain.model.GesturePoint

/**
 * Overlay component that displays the glide typing gesture path with Gboard-like visual effects
 */
@Composable
fun GlideTypingOverlay(
    gesturePath: List<GesturePoint>,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    pathColor: Color = MaterialTheme.colorScheme.primary,
    pathWidth: Float = 8.dp.value, // Increased width like Gboard
    showTrail: Boolean = true
) {
    // Animate the trail opacity for smooth fade effects
    val trailAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "trail_alpha"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        if (isActive && gesturePath.isNotEmpty() && showTrail) {
            // Draw the Gboard-style gesture path
            drawGboardStyleGesturePath(
                points = gesturePath,
                color = pathColor,
                strokeWidth = pathWidth,
                alpha = trailAlpha
            )
            
            // Draw Gboard-style start point
            drawGboardStartPoint(
                position = gesturePath.first().position,
                color = pathColor,
                alpha = trailAlpha
            )
            
            // Draw Gboard-style current position indicator
            if (gesturePath.size > 1) {
                drawGboardCurrentPoint(
                    position = gesturePath.last().position,
                    color = pathColor,
                    alpha = trailAlpha
                )
            }
        }
    }
}

/**
 * Draws the gesture path with Gboard-style visual effects
 */
private fun DrawScope.drawGboardStyleGesturePath(
    points: List<GesturePoint>,
    color: Color,
    strokeWidth: Float,
    alpha: Float
) {
    if (points.size < 2) return
    
    val path = Path()
    val firstPoint = points.first()
    path.moveTo(firstPoint.position.x, firstPoint.position.y)
    
    // Create smooth curves between points like Gboard
    for (i in 1 until points.size) {
        val currentPoint = points[i]
        val previousPoint = points[i - 1]
        
        if (i == 1) {
            // First segment - direct line
            path.lineTo(currentPoint.position.x, currentPoint.position.y)
        } else {
            // Smooth curve using quadratic bezier
            val midX = (previousPoint.position.x + currentPoint.position.x) / 2
            val midY = (previousPoint.position.y + currentPoint.position.y) / 2
            path.quadraticBezierTo(
                previousPoint.position.x, previousPoint.position.y,
                midX, midY
            )
        }
    }
    
    // Create gradient effect like Gboard (fading trail)
    val gradientColors = listOf(
        color.copy(alpha = alpha * 0.8f),
        color.copy(alpha = alpha * 0.6f),
        color.copy(alpha = alpha * 0.3f)
    )
    
    // Draw the main path with gradient effect
    drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = gradientColors,
            start = points.first().position,
            end = points.last().position
        ),
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
    
    // Draw additional glow effect like Gboard
    drawPath(
        path = path,
        color = color.copy(alpha = alpha * 0.3f),
        style = Stroke(
            width = strokeWidth * 1.5f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

/**
 * Draws the start point with Gboard-style visual effects
 */
private fun DrawScope.drawGboardStartPoint(position: Offset, color: Color, alpha: Float) {
    // Outer ring like Gboard
    drawCircle(
        color = color.copy(alpha = alpha * 0.8f),
        radius = 12.dp.toPx(),
        center = position,
        style = Stroke(width = 3.dp.toPx())
    )
    // Inner filled circle
    drawCircle(
        color = color.copy(alpha = alpha * 0.6f),
        radius = 8.dp.toPx(),
        center = position
    )
    // Center highlight
    drawCircle(
        color = Color.White.copy(alpha = alpha * 0.9f),
        radius = 4.dp.toPx(),
        center = position
    )
}

/**
 * Draws the current position indicator with Gboard-style visual effects
 */
private fun DrawScope.drawGboardCurrentPoint(position: Offset, color: Color, alpha: Float) {
    // Pulsing outer glow like Gboard
    drawCircle(
        color = color.copy(alpha = alpha * 0.4f),
        radius = 10.dp.toPx(),
        center = position
    )
    // Main indicator circle
    drawCircle(
        color = color.copy(alpha = alpha),
        radius = 6.dp.toPx(),
        center = position
    )
    // Center highlight
    drawCircle(
        color = Color.White.copy(alpha = alpha * 0.8f),
        radius = 3.dp.toPx(),
        center = position
    )
}

/**
 * Composable for displaying word predictions during glide typing
 */
@Composable
fun GlideTypingPredictions(
    predictions: List<String>,
    onPredictionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(predictions) { prediction ->
            Surface(
                onClick = { onPredictionSelected(prediction) },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = prediction,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}