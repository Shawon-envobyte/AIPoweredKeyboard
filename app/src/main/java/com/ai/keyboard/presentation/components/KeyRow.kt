package com.ai.keyboard.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.domain.model.GlideTypingState
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent

@Composable
fun KeyRow(
    keys: List<String>,
    onIntent: (KeyboardIntent) -> Unit,
    mode: KeyboardMode,
    modifier: Modifier = Modifier,
    isGestureTypingActive: Boolean = false, // Add gesture typing active state
    glideTypingState: GlideTypingState = GlideTypingState() // Add glide typing state
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        keys.forEach { char ->
            KeyButton(
                text = char,
                isGestureTypingActive= isGestureTypingActive,
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(char))) },
                onLongPress = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(it))) },
                mode = mode,
                isHighlighted = glideTypingState.highlightedKeys.contains(char),
                isCurrentKey = glideTypingState.currentKey == char,
                modifier = Modifier.weight(1f)
            )
        }
    }
}