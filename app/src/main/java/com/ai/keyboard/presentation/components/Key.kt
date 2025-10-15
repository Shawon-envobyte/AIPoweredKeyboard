package com.ai.keyboard.presentation.components

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.presentation.theme.AIKeyboardTheme

@Composable
fun Key(
    action: KeyAction,
    onClick: (KeyAction) -> Unit,
    modifier: Modifier = Modifier,
    isSpecial: Boolean = false
) {
    val backgroundColor = if (isSpecial) {
        AIKeyboardTheme.colors.specialKeyBackground
    } else {
        AIKeyboardTheme.colors.keyBackground
    }

    val textColor = if (isSpecial) {
        AIKeyboardTheme.colors.specialKeyText
    } else {
        AIKeyboardTheme.colors.keyText
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(48.dp)
            .shadow(2.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .clickable(
                onClick = { onClick(action) },
                interactionSource = interactionSource
            ),
        contentAlignment = Alignment.Center
    ) {
        when (action) {
            is KeyAction.Character -> {
                Text(
                    text = action.char,
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            is KeyAction.ImeAction -> {
                ImeKey(action.action)
            }
            else -> {
                // Handle other key
            }
        }
    }
}