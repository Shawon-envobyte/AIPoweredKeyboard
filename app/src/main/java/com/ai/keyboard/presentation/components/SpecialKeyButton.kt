package com.ai.keyboard.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.presentation.theme.AIKeyboardTheme

@Composable
fun SpecialKeyButton(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .padding(1.dp),
        shape = RoundedCornerShape(6.dp),
        color = AIKeyboardTheme.colors.specialKeyBackground,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.bodyMedium,
                color = AIKeyboardTheme.colors.specialKeyText,
                fontSize = 14.sp
            )
        }
    }
}