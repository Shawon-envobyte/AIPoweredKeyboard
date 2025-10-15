package com.ai.keyboard.presentation.components

import androidx.compose.ui.tooling.preview.Preview
import com.ai.keyboard.presentation.model.ActionButtonType
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.presentation.theme.DropdownGradient
import com.ai.keyboard.presentation.theme.VeryLightGray

@Composable
fun <T : Enum<T>> ActionButtonRow(
    modifier: Modifier = Modifier,
    actions: List<T>,
    selectedAction: T? = null,
    onActionClick: (T) -> Unit,
    labelProvider: (T) -> String,
    iconProvider: (T) -> Int? = { null },
    emojiProvider: (T) -> String? = { null },
    gradientProvider: (T) -> Boolean = { false }
) {
    var selected by remember { mutableStateOf(selectedAction) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        actions.forEach { action ->
            val isSelected = action == selected
            GenericActionButtonItem(
                label = labelProvider(action),
                icon = iconProvider(action),
                emoji = emojiProvider(action),
                isGradient = gradientProvider(action),
                isSelected = isSelected,
                onClick = {
                    selected = action
                    onActionClick(action)
                }
            )
        }
    }
}

@Composable
fun GenericActionButtonItem(
    label: String,
    icon: Int? = null,
    emoji: String? = null,
    isGradient: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)

    Row(
        modifier = Modifier
            .clip(shape)
            .background(
                brush = if (isSelected || isGradient)
                    DropdownGradient
                else
                    SolidColor(VeryLightGray)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        when {
            icon != null -> {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }

            emoji != null -> {
                Text(text = emoji, fontSize = 18.sp)
            }
        }

        Text(
            text = label,
            color = if (isSelected || isGradient)
                MaterialTheme.colorScheme.background
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ActionButtonRowPreview() {


    MaterialTheme {
        ActionButtonRow(
            actions = ActionButtonType.entries,
            selectedAction = ActionButtonType.REPHRASE,
            onActionClick = { println("Clicked: ${it.name}") },
            labelProvider = { it.label },
            iconProvider = { it.icon },
            emojiProvider = { it.emoji },
            gradientProvider = { it.isGradient }
        )
    }
}
