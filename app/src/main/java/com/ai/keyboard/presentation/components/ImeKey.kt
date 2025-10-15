package com.ai.keyboard.presentation.components

import android.view.inputmethod.EditorInfo
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ai.keyboard.presentation.theme.AIKeyboardTheme

@Composable
fun ImeKey(
    action: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val icon = when (action) {
        EditorInfo.IME_ACTION_SEARCH -> Icons.Default.Search
        EditorInfo.IME_ACTION_SEND -> Icons.AutoMirrored.Filled.Send
        EditorInfo.IME_ACTION_GO -> Icons.AutoMirrored.Filled.ArrowForward
        EditorInfo.IME_ACTION_DONE -> Icons.Default.Done
        EditorInfo.IME_ACTION_NEXT -> Icons.AutoMirrored.Filled.ArrowForward
        else -> Icons.Default.Check
    }

    val elevation by animateDpAsState(
        targetValue = 2.dp,
        animationSpec = tween(durationMillis = 50),
        label = "keyElevation"
    )

    val background by animateColorAsState(
        targetValue = AIKeyboardTheme.colors.specialKeyBackground,
        animationSpec = tween(durationMillis = 50),
        label = "keyBackground"
    )

    Surface(
        color = background,
        shape = RoundedCornerShape(6.dp),
        shadowElevation = elevation,
        onClick = onClick
    ) {
        Box(
            modifier = modifier
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AIKeyboardTheme.colors.specialKeyText,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
