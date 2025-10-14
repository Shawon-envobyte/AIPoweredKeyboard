package com.ai.keyboard.presentation.keyboard

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.presentation.components.Key
import com.ai.keyboard.presentation.components.KeyButton
import com.ai.keyboard.presentation.components.KeyRow
import com.ai.keyboard.presentation.components.SpecialKeyButton
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent

@Composable
fun SymbolKeyboard(
    onIntent: (KeyboardIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // First row
        KeyRow(
            keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
            onIntent = onIntent,
            mode = KeyboardMode.SYMBOLS
        )

        // Second row
        KeyRow(
            keys = listOf("-", "/", ":", ";", "(", ")", "$", "&", "@", "\""),
            onIntent = onIntent,
            mode = KeyboardMode.SYMBOLS
        )

        // Third row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SpecialKeyButton(
                icon = "=\\<",
                onClick = { onIntent(KeyboardIntent.ExtendedSymbolPressed) },
                modifier = Modifier.weight(1.5f)
            )

            listOf(".", ",", "?", "!", "'", "[", "]", "{", "}").forEach { char ->
                KeyButton(
                    text = char,
                    onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(char))) },
                    mode = KeyboardMode.SYMBOLS,
                    modifier = Modifier.weight(1f)
                )
            }

            SpecialKeyButton(
                icon = "⌫",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Backspace)) },
                modifier = Modifier.weight(1.5f)
            )
        }

        // Fourth row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SpecialKeyButton(
                icon = "ABC",
                onClick = { onIntent(KeyboardIntent.AlphabetPressed) },
                modifier = Modifier.weight(1.5f)
            )

            KeyButton(
                text = "#",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character("#"))) },
                mode = KeyboardMode.SYMBOLS,
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                text = " ",
                displayText = "Space",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Space)) },
                mode = KeyboardMode.SYMBOLS,
                modifier = Modifier.weight(4f)
            )

            KeyButton(
                text = "%",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character("%"))) },
                mode = KeyboardMode.SYMBOLS,
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