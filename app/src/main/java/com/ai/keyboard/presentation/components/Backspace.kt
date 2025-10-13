package com.ai.keyboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.presentation.theme.AIKeyboardTheme
import kotlinx.coroutines.delay

@Composable
fun Backspace(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRepeat: (() -> Unit)? = null,           // called during long-press
    initialDelay: Long = 200L,                // wait before repeating starts
    initialInterval: Long = 100L,             // first repeat interval
    minInterval: Long = 30L,                  // fastest repeat
    accelerateBy: Float = 0.90f               // 0.88 => ~12% faster each tick
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isRepeating by remember { mutableStateOf(false) }

    // Start/stop the repeating loop
    LaunchedEffect(isRepeating) {
        if (!isRepeating || onRepeat == null) return@LaunchedEffect
        delay(initialDelay)
        var interval = initialInterval
        while (isRepeating) {
            onRepeat()
            delay(interval)
            interval = (interval * accelerateBy).toLong().coerceAtLeast(minInterval)
        }
    }

    // End repeat when finger lifts or cancels
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is androidx.compose.foundation.interaction.PressInteraction.Release,
                is androidx.compose.foundation.interaction.PressInteraction.Cancel -> {
                    isRepeating = false
                }
            }
        }
    }

    val backgroundColor = AIKeyboardTheme.colors.specialKeyBackground
    val textColor =  AIKeyboardTheme.colors.specialKeyText

    Box(
        modifier = modifier
            .height(48.dp)
            .shadow(2.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = {
                    if (onRepeat != null) isRepeating = true
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
