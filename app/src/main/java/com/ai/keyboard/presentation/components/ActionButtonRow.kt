package com.ai.keyboard.presentation.components

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.R
import com.ai.keyboard.presentation.theme.DropdownGradient
import com.ai.keyboard.presentation.theme.VeryLightGray

@Composable
fun ActionButtonRow(
    modifier: Modifier = Modifier,
    actions: List<ActionButtonData>,
    onActionClick: (ActionButtonData) -> Unit
) {
    var selectedAction by remember { mutableStateOf<ActionButtonData?>(null) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        actions.forEach { action ->
            val isSelected = action == selectedAction
            ActionButtonItem(
                action = action,
                isSelected = isSelected,
                onClick = {
                    selectedAction = action
                    onActionClick(action)
                }
            )
        }
    }
}

@Composable
fun ActionButtonItem(
    action: ActionButtonData,
    isSelected: Boolean,
    onClick: () -> Unit
) {


    val shape = RoundedCornerShape(24.dp)

    Row(
        modifier = Modifier
            .clip(shape)
            .background(
                brush = if (isSelected || action.isGradient)
                    DropdownGradient
                else
                    SolidColor(VeryLightGray)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (action.icon != null) {
            Icon(
                painter = painterResource(id = action.icon),
                contentDescription = action.label,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        } else if (action.emoji != null) {
            Text(
                text = action.emoji,
                fontSize = 18.sp
            )
        }

        Text(
            text = action.label,
            color = if (isSelected || action.isGradient) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.6f
            ),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        )
    }
}

data class ActionButtonData(
    val label: String,
    val emoji: String? = null,
    val icon: Int? = null,
    val isGradient: Boolean = false
)

@Preview(showBackground = true)
@Composable
fun ActionButtonRowPreview() {
    val actions = listOf(
        ActionButtonData(label = "Rephrase", icon = R.drawable.ic_magic),
        ActionButtonData(label = "Grammar Fix", emoji = "🛠️"),
        ActionButtonData(label = "Add emoji", emoji = "🤗")
    )

    MaterialTheme {
        ActionButtonRow(
            actions = actions,
            onActionClick = { println("Clicked: ${it.label}") }
        )
    }
}
