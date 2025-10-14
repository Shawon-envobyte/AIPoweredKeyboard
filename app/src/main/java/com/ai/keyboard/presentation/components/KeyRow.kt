package com.ai.keyboard.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent
import com.ai.keyboard.presentation.theme.AIKeyboardTheme

@Composable
fun KeyRow(
    keys: List<String>,
    onIntent: (KeyboardIntent) -> Unit,
    mode: KeyboardMode,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        keys.forEach { char ->
            KeyButton(
                text = char,
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(char))) },
                mode = mode,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun KeyButton(
    text: String,
    onClick: () -> Unit,
    mode: KeyboardMode,
    modifier: Modifier = Modifier,
    displayText: String = text
) {
    val displayChar = when {
        displayText != text -> displayText
        mode == KeyboardMode.UPPERCASE || mode == KeyboardMode.CAPS_LOCK -> text.uppercase()
        else -> text
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .padding(1.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
        color = AIKeyboardTheme.colors.keyBackground,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = displayChar,
                style = MaterialTheme.typography.bodyLarge,
                color = AIKeyboardTheme.colors.keyText
            )
        }
    }
}