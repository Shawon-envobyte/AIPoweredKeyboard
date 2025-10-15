package com.ai.keyboard.presentation.screen.word_tone

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
import com.ai.keyboard.presentation.model.WordToneType
import com.ai.keyboard.presentation.screen.keyboard.KeyboardUIState
import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel

@Composable
fun WordToneScreen(
    uiState: KeyboardUIState,
    viewModel: KeyboardViewModel,
    onBackButtonClick: () -> Unit,
) {
    LaunchedEffect(key1 = Unit, key2 = uiState.language, key3 = uiState.selectedAction) {
        viewModel.getWordTone()

    }
    WordToneScreenContent(
        uiState = uiState,
        viewModel = viewModel,
        onBackButtonClick = onBackButtonClick,
    )
}

@Composable
fun WordToneScreenContent(
    uiState: KeyboardUIState,
    viewModel: KeyboardViewModel,
    onBackButtonClick: () -> Unit,
) {

    Column(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {



        CustomToolbar(
            title = stringResource(R.string.word_tone),
            onBackButtonClicked = onBackButtonClick,
            selectedLanguage = uiState.language,
            onLanguageSelected = { viewModel.onLanguageSelected(it) }
        )
        ActionButtonRow(
            actions = WordToneType.entries.map { it },
            selectedAction = WordToneType.REWRITE,
            onActionClick = { viewModel.onSelectedWordActionChange(it) },
            labelProvider = { it.label },
            iconProvider = { it.icon },
            emojiProvider = { it.emoji },
            gradientProvider = { it.isGradient }
        )
        ContentCard(
            showButton = true,
            text = uiState.correctedText,
            buttonText = "Replace Text",
            onButtonClick = { viewModel.replaceCurrentInputWith(uiState.correctedText) }
        )

        CustomBottomBar(
            onBackButtonClick = onBackButtonClick,
        )

    }

}
