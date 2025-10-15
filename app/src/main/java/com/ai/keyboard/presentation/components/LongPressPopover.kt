package com.ai.keyboard.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.ai.keyboard.presentation.theme.AIKeyboardTheme

@Composable
fun LongPressPopover(
    options: List<String>,
    onSelect: (String) -> Unit,
    keySize: IntSize
) {
    val density = LocalDensity.current

    // Pre-calculate dimensions once instead of in modifiers
    val dimensions = remember(keySize, options.size, density) {
        with(density) {
            PopoverDimensions(
                width = (keySize.width * options.size).toDp(),
                height = (keySize.height * 1.5f).toDp(),
                offsetY = -keySize.height + 8
            )
        }
    }

    // Instant animation for snappy feel
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "popoverScale"
    )

    Box(
        modifier = Modifier
            .width(dimensions.width)
            .height(dimensions.height)
            .offset { IntOffset(x = 0, y = dimensions.offsetY) }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
            .zIndex(10f)
            .background(AIKeyboardTheme.colors.keyText, RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            options.forEachIndexed { index, char ->
                CharOption(
                    char = char,
                    onSelect = onSelect,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CharOption(
    char: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Use interactionSource for instant visual feedback
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) AIKeyboardTheme.colors.keyBackground.copy(alpha = 0.2f)
        else Color.Transparent,
        animationSpec = tween(durationMillis = 50),
        label = "charBg"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null // Remove ripple for faster feel
            ) { onSelect(char) }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            color = AIKeyboardTheme.colors.keyBackground,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

private data class PopoverDimensions(
    val width: Dp,
    val height: Dp,
    val offsetY: Int
)