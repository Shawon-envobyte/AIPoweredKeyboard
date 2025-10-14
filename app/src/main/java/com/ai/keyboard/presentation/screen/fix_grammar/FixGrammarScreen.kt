package com.ai.keyboard.presentation.screen.fix_grammar

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
import com.ai.keyboard.presentation.model.ActionButtonType
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent
import com.ai.keyboard.presentation.screen.keyboard.KeyboardUIState
import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel

@Composable
fun FixGrammarScreen(
    uiState: KeyboardUIState,
    viewModel: KeyboardViewModel,
    onBackButtonClick: () -> Unit,
) {
    LaunchedEffect(key1 = Unit, key2 = uiState.language, key3 = uiState.selectedAction) {
        viewModel.getGrammar()

    }
    FixGrammarScreenContent(
        uiState = uiState,
        viewModel = viewModel,
        onBackButtonClick = onBackButtonClick,
    )
}

@Composable
fun FixGrammarScreenContent(
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
            actions = ActionButtonType.entries.map { it },
            selectedAction = uiState.selectedAction,
            onActionClick = { viewModel.onSelectedActionChange(it) }
        )
        Box(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            ContentCard(
                text = uiState.correctedText,
                buttonText = "Replace Text",
                onButtonClick = {
                    viewModel.handleIntent(
                        KeyboardIntent.SuggestionSelected(uiState.correctedText)
                    )
                }
            )
        }
        CustomBottomBar(
            onBackButtonClick = onBackButtonClick,
        )

    }

}

