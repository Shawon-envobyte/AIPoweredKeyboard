package com.ai.keyboard.presentation.screen.clipboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.domain.model.ClipboardItem
import com.ai.keyboard.domain.model.ClipboardState
import com.ai.keyboard.presentation.screen.keyboard.KeyboardUIState
import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel
import com.ai.keyboard.presentation.theme.AIKeyboardTheme


@Composable
fun ClipboardScreen(
    uiState: KeyboardUIState,
    viewModel: KeyboardViewModel,
    onBackButtonClick: () -> Unit,
    clipboardState: ClipboardState,
    onEditClick: () -> Unit,
    onItemClick: (ClipboardItem) -> Unit,
    onItemSelect: (ClipboardItem) -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    ClipBoardScreenContent(
        clipboardState = clipboardState,
        onBackClick = onBackButtonClick,
        onEditClick = onEditClick,
        onItemClick = onItemClick,
        onItemSelect = onItemSelect,
        onDeleteSelected = onDeleteSelected
    )
}


@Composable
fun ClipBoardScreenContent(
    clipboardState: ClipboardState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onItemClick: (ClipboardItem) -> Unit,
    onItemSelect: (ClipboardItem) -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Top bar with controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AIKeyboardTheme.colors.keyText
                )
            }

//            // Toggle clipboard button
//            IconButton(
//                onClick = onToggleClipboard,
//                modifier = Modifier.size(40.dp)
//            ) {
//                Icon(
//                    if (clipboardState.isEnabled) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
//                    contentDescription = "Toggle Clipboard",
//                    tint = if (clipboardState.isEnabled)
//                        MaterialTheme.colorScheme.primary
//                    else
//                        AIKeyboardTheme.colors.keyText
//                )
//            }

            // Edit button
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = if (clipboardState.isEditMode)
                        MaterialTheme.colorScheme.primary
                    else
                        AIKeyboardTheme.colors.keyText
                )
            }
        }

        // Delete button (shown in edit mode)
        if (clipboardState.isEditMode && clipboardState.selectedItems.isNotEmpty()) {
            Button(
                onClick = onDeleteSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Selected (${clipboardState.selectedItems.size})")
            }
        }

        // Clipboard items
        if (clipboardState.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No clipboard items",
                    color = AIKeyboardTheme.colors.keyText.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.height(120.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(clipboardState.items) { item ->
                    ClipboardItemCard(
                        item = item,
                        isEditMode = clipboardState.isEditMode,
                        isSelected = item.id in clipboardState.selectedItems,
                        onClick = {
                            if (clipboardState.isEditMode) {
                                onItemSelect(item)
                            } else {
                                onItemClick(item)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClipboardItemCard(
    item: ClipboardItem,
    isEditMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                AIKeyboardTheme.colors.suggestionBackground
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Text(
                text = item.text,
                color = AIKeyboardTheme.colors.suggestionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}