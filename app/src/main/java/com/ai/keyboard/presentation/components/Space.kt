package com.ai.keyboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.presentation.theme.AIKeyboardTheme

@Composable
fun Space(
    modifier: Modifier = Modifier,
    text: String = "Space",
    onClick: () -> Unit,
    onSwipe: (Int) -> Unit
) {
    val backgroundColor = AIKeyboardTheme.colors.specialKeyBackground
    val textColor = AIKeyboardTheme.colors.specialKeyText
    var accumulatedDrag by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .height(48.dp)
            .shadow(2.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
            )
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { accumulatedDrag = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        accumulatedDrag += dragAmount
                        val threshold = 20f
                        val moves = (accumulatedDrag / threshold).toInt()
                        if (moves != 0) {
                            onSwipe(moves)
                            accumulatedDrag -= moves * threshold
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}