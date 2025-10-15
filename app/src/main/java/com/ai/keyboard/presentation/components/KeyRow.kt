package com.ai.keyboard.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent

@Composable
fun KeyRow(
    keys: List<String>,
    onIntent: (KeyboardIntent) -> Unit,
    mode: KeyboardMode,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        keys.forEach { char ->
            KeyButton(
                text = char,
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(char))) },
                onLongPress = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(it))) },
                mode = mode,
                modifier = Modifier.weight(1f)
            )
        }
    }
}