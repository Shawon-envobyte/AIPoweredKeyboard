package com.ai.keyboard.presentation.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.presentation.components.Backspace
import com.ai.keyboard.presentation.components.Key
import com.ai.keyboard.presentation.components.KeyButton
import com.ai.keyboard.presentation.components.KeyRow
import com.ai.keyboard.presentation.components.Space
import com.ai.keyboard.presentation.components.SpecialKeyButton
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent

@Composable
fun AlphabeticKeyboard(
    mode: KeyboardMode,
    isNumberRowEnabled: Boolean,
    onIntent: (KeyboardIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Optional Number Row
        if (isNumberRowEnabled) {
            KeyRow(
                keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
                onIntent = onIntent,
                mode = mode
            )
        }

        // First row: Q W E R T Y U I O P
        KeyRow(
            keys = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
            onIntent = onIntent,
            mode = mode
        )

        // Second row: A S D F G H J K L
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l").forEach { char ->
                KeyButton(
                    text = char,
                    onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(char))) },
                    mode = mode,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Third row: Shift Z X C V B N M Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Shift key
            SpecialKeyButton(
                icon = when (mode) {
                    KeyboardMode.CAPS_LOCK -> "⇪"
                    KeyboardMode.UPPERCASE -> "⇧"
                    else -> "⇧"
                },
                onClick = { onIntent(KeyboardIntent.ShiftPressed) },
                isActive = mode == KeyboardMode.UPPERCASE || mode == KeyboardMode.CAPS_LOCK,
                modifier = Modifier.weight(1.5f)
            )

            listOf("z", "x", "c", "v", "b", "n", "m").forEach { char ->
                KeyButton(
                    text = char,
                    onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(char))) },
                    mode = mode,
                    modifier = Modifier.weight(1f)
                )
            }

            // Backspace key
            Backspace(
                modifier = Modifier.weight(1.5f),
                text = "⌫",
                onClick = {
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.Backspace))
                },
                onRepeat = {
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.Backspace))
                }
            )
        }

        // Fourth row: 123 , Space . Enter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SpecialKeyButton(
                icon = "123",
                onClick = { onIntent(KeyboardIntent.SymbolPressed) },
                modifier = Modifier.weight(1.5f)
            )

            KeyButton(
                text = ",",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(","))) },
                mode = mode,
                modifier = Modifier.weight(1f)
            )

            Space(
                modifier = Modifier.weight(4f),
                text = "Space",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Space)) },
                onSwipe = { amount ->
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.MoveCursor(amount)))
                }
            )

            KeyButton(
                text = ".",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character("."))) },
                mode = mode,
                modifier = Modifier.weight(1f)
            )

            SpecialKeyButton(
                icon = "⏎",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Enter)) },
                modifier = Modifier.weight(1.5f)
            )
        }
    }
}

private val numberRowKeys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

private val lowerCaseKeys = listOf(
    listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
    listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
    listOf("z", "x", "c", "v", "b", "n", "m")
)

private val upperCaseKeys = listOf(
    listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
    listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
    listOf("Z", "X", "C", "V", "B", "N", "M")
)