package com.ai.keyboard.presentation.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.presentation.components.Backspace
import com.ai.keyboard.presentation.components.Key
import com.ai.keyboard.presentation.components.KeyRow
import com.ai.keyboard.presentation.components.Space
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent

@Composable
fun AlphabeticKeyboard(
    mode: KeyboardMode,
    isNumberRowEnabled: Boolean = false,
    onIntent: (KeyboardIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val keys = when (mode) {
        KeyboardMode.UPPERCASE,
        KeyboardMode.CAPS_LOCK -> upperCaseKeys
        else -> lowerCaseKeys
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        // 🔢 Optional Number Row
        if (isNumberRowEnabled) {
            KeyRow(
                keys = numberRowKeys,
                onKeyPress = { key ->
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(key)))
                }
            )
        }

        // 🔠 Alphabet Rows
        keys.forEachIndexed { rowIndex, rowKeys ->
            when (rowIndex) {
                // Row 1 & Row 2 (standard letters)
                0, 1 -> KeyRow(
                    keys = rowKeys,
                    onKeyPress = { key ->
                        onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(key)))
                    }
                )

                // Row 3 (Shift + Letters + Backspace)
                2 -> Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Key(
                        text = "⇧",
                        onClick = { onIntent(KeyboardIntent.ShiftPressed) },
                        modifier = Modifier.weight(1.5f),
                        isSpecial = true
                    )

                    rowKeys.forEach { key ->
                        Key(
                            text = key,
                            onClick = {
                                onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(key)))
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

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
            }
        }

        // 🪄 Bottom Row (Symbols, Space, Enter)
        BottomKeyboardRow(onIntent)
    }
}

@Composable
private fun BottomKeyboardRow(
    onIntent: (KeyboardIntent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Key(
            text = "123",
            onClick = { onIntent(KeyboardIntent.SymbolPressed) },
            modifier = Modifier.weight(1.5f),
            isSpecial = true
        )

        Space(
            modifier = Modifier.weight(5f),
            text = "Space",
            onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Space)) },
            onSwipe = { amount ->
                onIntent(KeyboardIntent.KeyPressed(KeyAction.MoveCursor(amount)))
            }
        )

        Key(
            text = "↵",
            onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Enter)) },
            modifier = Modifier.weight(1.5f),
            isSpecial = true
        )
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