package com.ai.keyboard.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.presentation.theme.AIKeyboardTheme
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
    longPressDelay: Long = 300L, // Reduced from 500L for faster response
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
    var showPopup by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    var keySize by remember { mutableStateOf(IntSize.Zero) }
    var keyOffset by remember { mutableStateOf(IntOffset.Zero) }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Cache long press availability
    val hasLongPressOptions = remember(text) { longPressMap.containsKey(text) }

    // Instant visual feedback animation
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 0.dp else 2.dp,
        label = "keyElevation"
    )

    val keyBackground by animateColorAsState(
        targetValue = if (isPressed) AIKeyboardTheme.colors.background else AIKeyboardTheme.colors.keyBackground,
        label = "keyBackground"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "keyScale"
    )

    Box(
        modifier = modifier.height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        // --- Main key surface ---
        Surface(
            color = keyBackground,
            shape = RoundedCornerShape(6.dp),
            shadowElevation = elevation,
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .onGloballyPositioned { coords ->
                    keyOffset = IntOffset(
                        coords.localToWindow(Offset.Zero).x.toInt(),
                        coords.localToWindow(Offset.Zero).y.toInt()
                    )
                    keySize = coords.size
                }
                .pointerInput(text, hasLongPressOptions) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            val released = try {
                                tryAwaitRelease()
                            } finally {
                                isPressed = false
                            }
                        },
                        onTap = {
                            showTooltip = true
                            onClick()
                            // Reduced delay for snappier feel
                            coroutineScope.launch {
                                delay(250)
                            }
                            showTooltip = false
                        },
                        onLongPress = {
                            if (hasLongPressOptions) {
                                showPopup = true
                                showTooltip = false
                            }
                        }
                    )
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = displayChar,
                    color = AIKeyboardTheme.colors.keyText,
                    fontSize = 18.sp
                )
            }
        }

        // --- Tooltip Popup ---
        if (showTooltip && keySize != IntSize.Zero) {
            val popupWidthDp = with(density) { keySize.width.toDp() * 1.25f }
            val popupHeightDp = with(density) { keySize.height.toDp() * 1.25f }
            val popupYOffsetPx = with(density) { -(keySize.height.toDp() + 10.dp).roundToPx() }

            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, popupYOffsetPx),
            ) {
                KeyPopupBox(
                    modifier = Modifier.size(popupWidthDp, popupHeightDp),
                    text = displayChar
                )
            }
        }

        // --- Long Press Popup ---
        if (showPopup && hasLongPressOptions) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(keyOffset.x, keyOffset.y - keySize.height * 2),
                onDismissRequest = { showPopup = false }
            ) {
                LongPressPopover(
                    options = longPressMap[text] ?: emptyList(),
                    onSelect = { char ->
                        onLongPress?.invoke(char)
                        showPopup = false
                    },
                    keySize = keySize
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
    // Instant popup animation
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "tooltipScale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
            .background(
                AIKeyboardTheme.colors.keyText,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = AIKeyboardTheme.colors.keyBackground,
            fontSize = 28.sp,
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