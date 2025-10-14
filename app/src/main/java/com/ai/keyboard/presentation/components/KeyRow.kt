package com.ai.keyboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent
import com.ai.keyboard.presentation.theme.AIKeyboardTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    var showPopup by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var keySize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .height(48.dp)
            .onGloballyPositioned { keySize = it.size },
        contentAlignment = Alignment.Center
    ) {
        // --- Main key surface ---
        Surface(
            onClick = {
                showPopup = true
                onClick()
                coroutineScope.launch {
                    delay(120)
                    showPopup = false
                }
            },
            color = AIKeyboardTheme.colors.keyBackground,
            shape = RoundedCornerShape(6.dp),
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = displayChar,
                    color = AIKeyboardTheme.colors.keyText,
                    fontSize = 18.sp
                )
            }
        }

        // --- Popup ---
        if (showPopup) {
            val popupWidthDp = with(density) { (keySize.width.toDp() * 3.5f) }
            val popupHeightDp = with(density) { (keySize.height.toDp() * 3.5f) }
            val popupYOffsetPx = with(density) { (keySize.height.toDp() + 8.dp).roundToPx() }

            Box(
                modifier = Modifier
                    .width(popupWidthDp)
                    .height(popupHeightDp)
                    .offset { IntOffset(0, -popupYOffsetPx) }
                    .zIndex(10f)
                    .background(
                        AIKeyboardTheme.colors.keyText.copy(alpha = 0.95f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text.uppercase(),
                    color = AIKeyboardTheme.colors.keyBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}