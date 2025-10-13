package com.ai.keyboard.presentation.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.presentation.components.Key
import com.ai.keyboard.presentation.components.KeyRow
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent

@Composable
fun ExtendedSymbolKeyboard(
    onIntent: (KeyboardIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: Mathematical and currency symbols
        KeyRow(
            keys = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆"),
            onKeyPress = { key ->
                onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(key)))
            }
        )

        // Row 2: Extended punctuation and symbols
        KeyRow(
            keys = listOf("£", "¢", "€", "¥", "^", "°", "=", "{", "}", "\\"),
            onKeyPress = { key ->
                onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(key)))
            }
        )

        // Row 3: More symbols with toggle back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Key(
                text = "123",
                onClick = { onIntent(KeyboardIntent.SymbolPressed) },
                modifier = Modifier.weight(1.5f),
                isSpecial = true
            )

            listOf("%", "©", "®", "™", "✓", "[", "]", "<", ">").forEach { key ->
                Key(
                    text = key,
                    onClick = {
                        onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(key)))
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Key(
                text = "⌫",
                onClick = {
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.Backspace))
                },
                modifier = Modifier.weight(1.5f),
                isSpecial = true
            )
        }

        // Row 4: Bottom row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Key(
                text = "ABC",
                onClick = { onIntent(KeyboardIntent.AlphabetPressed) },
                modifier = Modifier.weight(1.5f),
                isSpecial = true
            )

            Key(
                text = "Space",
                onClick = {
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.Space))
                },
                modifier = Modifier.weight(5f),
                isSpecial = true
            )

            Key(
                text = "↵",
                onClick = {
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.Enter))
                },
                modifier = Modifier.weight(1.5f),
                isSpecial = true
            )
        }
    }
}