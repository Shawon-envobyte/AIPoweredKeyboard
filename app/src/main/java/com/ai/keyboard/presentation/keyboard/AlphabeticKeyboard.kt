package com.ai.keyboard.presentation.keyboard

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.presentation.components.BackspaceKey
import com.ai.keyboard.presentation.components.ImeKey
import com.ai.keyboard.presentation.components.KeyButton
import com.ai.keyboard.presentation.components.KeyRow
import com.ai.keyboard.presentation.components.SpaceKey
import com.ai.keyboard.presentation.components.SpecialKeyButton
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent

@Composable
fun AlphabeticKeyboard(
    mode: KeyboardMode,
    isNumberRowEnabled: Boolean,
    onIntent: (KeyboardIntent) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: Int
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { clip = false },
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (isNumberRowEnabled) {
            KeyRow(
                keys = numberRowKeys,
                onIntent = onIntent,
                mode = mode
            )
        }

        KeyRow(
            keys = lowerCaseKeys[0],
            onIntent = onIntent,
            mode = mode
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            lowerCaseKeys[1].forEach { char ->
                KeyButton(
                    text = char,
                    onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(char))) },
                    mode = mode,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
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

            Spacer(modifier = Modifier.width(3.dp))

            lowerCaseKeys[2].forEach { char ->
                KeyButton(
                    text = char,
                    onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(char))) },
                    mode = mode,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.width(3.dp))

            BackspaceKey(
                modifier = Modifier.weight(1.5f),
                text = "⌫",
                onClick = {
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.Backspace))
                },
                onRepeat = {
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.Backspace))
                },
                onSelectionSwipe = { amount ->
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.SelectAndDelete(amount)))
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            SpecialKeyButton(
                icon = "123",
                onClick = { onIntent(KeyboardIntent.SymbolPressed) },
                modifier = Modifier.weight(1.5f)
            )

            Spacer(modifier = Modifier.width(3.dp))

            KeyButton(
                text = ",",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(","))) },
                mode = mode,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(3.dp))

            SpaceKey(
                modifier = Modifier.weight(4f),
                text = "Space",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Space)) },
                onSwipe = { amount ->
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.MoveCursor(amount)))
                }
            )

            Spacer(modifier = Modifier.width(3.dp))

            KeyButton(
                text = ".",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Character("."))) },
                mode = mode,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(3.dp))

            if (imeAction != EditorInfo.IME_ACTION_NONE && imeAction != EditorInfo.IME_ACTION_UNSPECIFIED) {
                ImeKey(
                    action = imeAction,
                    onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.ImeAction(imeAction))) },
                    modifier = Modifier.weight(1.5f)
                )
            } else {
                SpecialKeyButton(
                    icon = "⏎",
                    onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Enter)) },
                    modifier = Modifier.weight(1.5f)
                )
            }
        }
    }
}

private val numberRowKeys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

private val lowerCaseKeys = listOf(
    listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
    listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
    listOf("z", "x", "c", "v", "b", "n", "m")
)
