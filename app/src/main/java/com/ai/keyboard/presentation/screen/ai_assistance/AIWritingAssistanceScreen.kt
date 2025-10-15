package com.ai.keyboard.presentation.screen.ai_assistance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ai.keyboard.R
import com.ai.keyboard.presentation.components.ActionButtonRow
import com.ai.keyboard.presentation.components.ContentCard
import com.ai.keyboard.presentation.components.CustomBottomBar
import com.ai.keyboard.presentation.components.CustomToolbar
import com.ai.keyboard.presentation.model.AIWritingAssistanceType
import com.ai.keyboard.presentation.screen.keyboard.KeyboardUIState
import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel

@Composable
fun AIWritingAssistanceScreen(
    uiState: KeyboardUIState,
    viewModel: KeyboardViewModel,
    onBackButtonClick: () -> Unit,
) {
    LaunchedEffect(key1 = Unit, key2 = uiState.language, key3 = uiState.selectedAction) {
        viewModel.getAiAssistance()

    }
    AIWritingAssistanceScreenContent(
        uiState = uiState,
        viewModel = viewModel,
        onBackButtonClick = onBackButtonClick,
    )
}

@Composable
fun AIWritingAssistanceScreenContent(
    uiState: KeyboardUIState,
    viewModel: KeyboardViewModel,
    onBackButtonClick: () -> Unit,
) {

    Column {


        CustomToolbar(
            title = stringResource(R.string.fix_grammar),
            onBackButtonClicked = onBackButtonClick,
            selectedLanguage = uiState.language,
            onLanguageSelected = { viewModel.onLanguageSelected(it) }
        )
        ActionButtonRow(
            actions = AIWritingAssistanceType.entries.map { it },
            selectedAction = uiState.selectedAiAction,
            onActionClick = { viewModel.onSelectedAiActionChange(it) },
            labelProvider = { it.label },
            iconProvider = { it.icon },
            emojiProvider = { it.emoji },
            gradientProvider = { it.isGradient }
        )
        Box(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            ContentCard(
                text = uiState.correctedText,
                buttonText = "Replace Text",
                onButtonClick = {
                    viewModel.replaceCurrentInputWith(uiState.correctedText)
                }
            )
        }
        CustomBottomBar(
            onBackButtonClick = onBackButtonClick,
        )

    }

}

