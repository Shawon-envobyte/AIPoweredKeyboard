package com.ai.keyboard.presentation.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.presentation.theme.AIKeyboardTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun KeyButton(
    text: String,
    onClick: () -> Unit,
    mode: KeyboardMode,
    modifier: Modifier = Modifier,
    displayText: String = text,
    onLongPress: ((String) -> Unit)? = null,
    isHighlighted: Boolean = false, // For glide typing highlighting
    isCurrentKey: Boolean = false, // For current key under gesture
    isGestureTypingActive: Boolean = false, // Disable long press during gesture typing
) {
    // Memoize display character
    val displayChar = remember(displayText, text, mode) {
        when {
            displayText != text -> displayText
            mode == KeyboardMode.UPPERCASE || mode == KeyboardMode.CAPS_LOCK -> text.uppercase()
            else -> text
        }
    }

    var showTooltip by remember { mutableStateOf(false) }
    var tooltipCounter by remember { mutableIntStateOf(0) }
    var showPopup by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var keySize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    // Cache long press availability
    val hasLongPressOptions = remember(text) { longPressMap.containsKey(text) || text == "v" }

    // Instant visual feedback animation
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 0.dp else 2.dp,
        animationSpec = tween(durationMillis = 50),
        label = "keyElevation"
    )

    val background by animateColorAsState(
        targetValue = when {
            isCurrentKey -> AIKeyboardTheme.colors.keyBackground.copy(alpha = 0.9f) // Current key under gesture
            isHighlighted -> AIKeyboardTheme.colors.keyBackground.copy(alpha = 0.7f) // Highlighted by gesture path
            isPressed -> AIKeyboardTheme.colors.keyBackground
            else -> AIKeyboardTheme.colors.keyBackground
        },
        animationSpec = tween(durationMillis = 100),
        label = "keyBackground"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isCurrentKey -> AIKeyboardTheme.colors.keyText.copy(alpha = 1f)
            isHighlighted -> AIKeyboardTheme.colors.keyText.copy(alpha = 0.8f)
            else -> AIKeyboardTheme.colors.keyText
        },
        animationSpec = tween(durationMillis = 100),
        label = "keyTextColor"
    )

    Box(
        modifier = modifier.height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        // --- Main key surface ---
        Surface(
            color = background,
            shape = RoundedCornerShape(6.dp),
            shadowElevation = elevation,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 3.dp, vertical = 1.dp)
                .onGloballyPositioned { coords ->
                    keySize = coords.size
                }
                .pointerInput(text, hasLongPressOptions) {
                    var tooltipJob: Job? = null
                    var longPressJob: Job? = null
                    var isLongPressTriggered = false

                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)

                            when (event.type) {
                                PointerEventType.Press -> {
                                    tooltipJob?.cancel()
                                    longPressJob?.cancel()
                                    isLongPressTriggered = false

                                    isPressed = true
                                    tooltipCounter++
                                    showTooltip = true
                                    Log.d("KeyButton", "gestures: $isGestureTypingActive")
                                    // Start long press timer
                                    if (hasLongPressOptions && !isGestureTypingActive) {
                                        Log.d("KeyButton", "KeyButton: long press started")
                                        longPressJob = coroutineScope.launch {
                                            delay(1000)
                                            // Double-check that gesture typing hasn't started during the delay
                                            if (!isGestureTypingActive) {
                                                isLongPressTriggered = true
                                                showTooltip = false

                                                // Special case for 'v' key - direct paste without popup
                                                if (text == "v") {
                                                    onLongPress?.invoke("v")
                                                } else {
                                                    showPopup = true
                                                }
                                            }
                                        }
                                    }
                                }

                                PointerEventType.Release -> {
                                    longPressJob?.cancel()

                                    if (!isLongPressTriggered) {
                                        // Normal tap
                                        onClick()
                                        tooltipJob = coroutineScope.launch {
                                            delay(50)
                                            isPressed = false
                                            showTooltip = false
                                        }
                                    } else {
                                        // Long press was shown, hide everything
                                        isPressed = false
                                        showTooltip = false
                                    }
                                }

                                PointerEventType.Move -> {
                                    // If popup is showing, pass events to it
                                    if (showPopup) {
                                        event.changes.forEach { it.consume() }
                                    }
                                }
                            }
                        }
                    }
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = displayChar,
                    color = textColor,
                    fontSize = 22.sp
                )
            }
        }

        // --- Tooltip Popup ---
        if (showTooltip && keySize != IntSize.Zero) {
            val popupWidthDp = with(density) { keySize.width.toDp() * 1.5f }
            val popupHeightDp = with(density) { keySize.height.toDp() * 1.25f }
            val popupYOffsetPx = with(density) { -(keySize.height.toDp() + 10.dp).roundToPx() }

            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, popupYOffsetPx),
            ) {
                // Use counter to force recomposition on every show
                key(tooltipCounter) {
                    KeyPopupBox(
                        modifier = Modifier.size(popupWidthDp, popupHeightDp),
                        text = displayChar
                    )
                }
            }
        }

        // --- Long Press Popup ---
        if (showPopup && hasLongPressOptions) {
            val popupYOffset = with(density) { -(keySize.height.toDp() + 24.dp).roundToPx() }

            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, popupYOffset),
                onDismissRequest = { showPopup = false }
            ) {
                LongPressPopover(
                    options = longPressMap[text] ?: emptyList(),
                    onSelect = { char ->
                        onLongPress?.invoke(char)
                        showPopup = false
                        isPressed = false
                    },
                    onDismiss = {
                        showPopup = false
                        isPressed = false
                    }
                )
            }
        }
    }
}

@Composable
fun KeyPopupBox(
    modifier: Modifier,
    text: String
) {
    Box(
        modifier = modifier
            .background(
                AIKeyboardTheme.colors.keyText,
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = AIKeyboardTheme.colors.keyBackground,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

val longPressMap: Map<String, List<String>> = mapOf(
    "a" to listOf("á", "à", "â", "ä", "ã", "å", "ā"),
    "e" to listOf("é", "è", "ê", "ë", "ē", "ė", "ę"),
    "i" to listOf("í", "ì", "î", "ï", "ī", "į", "ı"),
    "o" to listOf("ó", "ò", "ô", "ö", "õ", "ō", "ø"),
    "u" to listOf("ú", "ù", "û", "ü", "ū"),
    "c" to listOf("ç", "ć", "č"),
    "n" to listOf("ñ", "ń"),
    "s" to listOf("ś", "š", "ß"),
    "y" to listOf("ý", "ÿ"),
    "z" to listOf("ź", "ž", "ż"),
    "d" to listOf("ď", "đ"),
    "g" to listOf("ğ"),
    "l" to listOf("ł"),
    "r" to listOf("ř"),
    "t" to listOf("ť")
)