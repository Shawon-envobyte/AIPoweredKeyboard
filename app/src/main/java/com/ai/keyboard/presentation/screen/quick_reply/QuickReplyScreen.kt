package com.ai.keyboard.presentation.screen.quick_reply

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.R
import com.ai.keyboard.presentation.components.ActionButtonRow
import com.ai.keyboard.presentation.components.CustomBottomBar
import com.ai.keyboard.presentation.components.CustomToolbar
import com.ai.keyboard.presentation.model.QuickReplyModule
import com.ai.keyboard.presentation.screen.keyboard.KeyboardUIState
import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel


@Composable
fun QuickReplyScreen(
    uiState: KeyboardUIState,
    viewModel: KeyboardViewModel,
    onBackButtonClick: () -> Unit,
) {

    QuickReplyScreenContent(
        uiState = uiState,
        viewModel = viewModel,
        onBackButtonClick = onBackButtonClick,
    )

}


@Composable
fun QuickReplyScreenContent(
    uiState: KeyboardUIState,
    viewModel: KeyboardViewModel,
    onBackButtonClick: () -> Unit,
) {
    Column {
        CustomToolbar(
            title = stringResource(R.string.quick_reply),
            onBackButtonClicked = onBackButtonClick,
            selectedLanguage = uiState.language,
            onLanguageSelected = { viewModel.onLanguageSelected(it) }
        )
    }
    ActionButtonRow(
        actions = QuickReplyModule.entries.map { it },
        selectedAction = uiState.selectQuickReplyAction,
        onActionClick = { viewModel.changeQuickReplyMessageType(it) },
        labelProvider = { it.label },
        iconProvider = { it.icon },
        emojiProvider = { it.emoji },
        gradientProvider = { it.isGradient }
    )
    Column {

        if (uiState.selectQuickReplyAction == QuickReplyModule.POSITIVE) {
            uiState.quickReplyList.positive.forEach {
                QuickReplyItem(message = it, onClick = { viewModel.replaceCurrentInputWith(it) })
            }
        } else if (uiState.selectQuickReplyAction == QuickReplyModule.NEGATIVE) {
            uiState.quickReplyList.negative.forEach {
                QuickReplyItem(message = it, onClick = { viewModel.replaceCurrentInputWith(it) })
            }
        } else {
            uiState.quickReplyList.neutral.forEach {
                QuickReplyItem(message = it, onClick = { viewModel.replaceCurrentInputWith(it) })
            }
        }

    }
    CustomBottomBar(
        onBackButtonClick = onBackButtonClick,
    )
}


@Composable
fun QuickReplyItem(
    message: String,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceBright,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onClick(message) } // handle click
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.typography.bodyMedium.color,
            fontSize = 15.sp
        )
    }
}
