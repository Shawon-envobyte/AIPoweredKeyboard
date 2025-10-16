package com.ai.keyboard.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.presentation.theme.AIKeyboardTheme
import kotlin.math.roundToInt

/**
 * Real-time word prediction popup that follows the gesture like Gboard
 */
@Composable
fun GestureWordPopup(
    predictions: List<String>,
    currentPosition: Offset?,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // Animate popup visibility
    val popupAlpha by animateFloatAsState(
        targetValue = if (isVisible && predictions.isNotEmpty()) 1f else 0f,
        animationSpec = tween(durationMillis = 150),
        label = "popup_alpha"
    )
    
    if (currentPosition != null && predictions.isNotEmpty()) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = isVisible && predictions.isNotEmpty(),
                enter = fadeIn(animationSpec = tween(150)) + scaleIn(
                    animationSpec = tween(150),
                    initialScale = 0.8f
                ),
                exit = fadeOut(animationSpec = tween(100)) + scaleOut(
                    animationSpec = tween(100),
                    targetScale = 0.8f
                ),
                modifier = Modifier
                    .offset {
                        with(density) {
                            // Position popup above the current gesture position
                            val offsetX = (currentPosition.x - 60.dp.toPx()).roundToInt()
                            val offsetY = (currentPosition.y - 80.dp.toPx()).roundToInt()
                            IntOffset(offsetX, offsetY)
                        }
                    }
                    .alpha(popupAlpha)
            ) {
                GboardStylePredictionPopup(
                    predictions = predictions.take(3) // Show top 3 predictions like Gboard
                )
            }
        }
    }
}

/**
 * Gboard-style prediction popup with rounded corners and shadow
 */
@Composable
private fun GboardStylePredictionPopup(
    predictions: List<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .wrapContentSize()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(12.dp),
        color = AIKeyboardTheme.colors.popupBackground,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            predictions.forEachIndexed { index, prediction ->
                PredictionItem(
                    text = prediction,
                    isPrimary = index == 0, // First prediction is primary like Gboard
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Individual prediction item with Gboard-style styling
 */
@Composable
private fun PredictionItem(
    text: String,
    isPrimary: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = if (isPrimary) 16.sp else 14.sp,
            fontWeight = if (isPrimary) FontWeight.SemiBold else FontWeight.Normal
        ),
        color = if (isPrimary) {
            AIKeyboardTheme.colors.popupText
        } else {
            AIKeyboardTheme.colors.popupText.copy(alpha = 0.8f)
        }
    )
}