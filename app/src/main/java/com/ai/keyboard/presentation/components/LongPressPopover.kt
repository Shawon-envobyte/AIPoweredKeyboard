package com.ai.keyboard.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.presentation.theme.AIKeyboardTheme

@Composable
fun LongPressPopover(
    options: List<String>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var highlightedIndex by remember { mutableIntStateOf(0) }
    val charRects = remember { mutableStateMapOf<Int, Rect>() }

    Surface(
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(12.dp),
        color = AIKeyboardTheme.colors.keyBackground,
        modifier = Modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: continue

                        when (event.type) {
                            PointerEventType.Move -> {
                                val localPosition = change.position
                                // Find which character is under the finger
                                val newIndex = charRects.entries
                                    .firstOrNull { (_, rect) ->
                                        rect.contains(localPosition)
                                    }
                                    ?.key

                                if (newIndex != null) {
                                    highlightedIndex = newIndex
                                }
                            }

                            PointerEventType.Release -> {
                                val selectedChar = options.getOrNull(highlightedIndex)
                                if (selectedChar != null) {
                                    onSelect(selectedChar)
                                } else {
                                    onDismiss()
                                }
                                return@awaitPointerEventScope
                            }
                        }
                    }
                }
            }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, char ->
                val isHighlighted = index == highlightedIndex

                val backgroundColor by animateColorAsState(
                    targetValue = if (isHighlighted) AIKeyboardTheme.colors.keyText else Color.Transparent,
                    animationSpec = tween(durationMillis = 100),
                    label = "highlightBackground"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isHighlighted) AIKeyboardTheme.colors.keyBackground else AIKeyboardTheme.colors.keyText,
                    animationSpec = tween(durationMillis = 100),
                    label = "highlightText"
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            charRects[index] = layoutCoordinates.boundsInParent()
                        }
                        .background(backgroundColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = char,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }
    }
}