package com.ai.keyboard.presentation.screen.translate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ai.keyboard.R
import com.ai.keyboard.presentation.components.ContentCard
import com.ai.keyboard.presentation.components.CustomBottomBar
import com.ai.keyboard.presentation.components.CustomToolbar
import com.ai.keyboard.presentation.screen.keyboard.KeyboardUIState
import com.ai.keyboard.presentation.screen.keyboard.KeyboardViewModel

@Composable
fun TranslateScreen(
    uiState: KeyboardUIState,
    viewModel: KeyboardViewModel,
    onBackButtonClick: () -> Unit,
) {
    LaunchedEffect(key1 = Unit, key2 = uiState.language, key3 = uiState.selectedAction) {
        viewModel.getWordTone()

    }
    TranslateScreenContent(
        uiState = uiState,
        viewModel = viewModel,
        onBackButtonClick = onBackButtonClick,
    )
}

@Composable
fun TranslateScreenContent(
    uiState: KeyboardUIState,
    viewModel: KeyboardViewModel,
    onBackButtonClick: () -> Unit,
) {

    Column(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {


        CustomToolbar(
            title = "Translate",
            onBackButtonClicked = onBackButtonClick,
            selectedLanguage = uiState.language,
            onLanguageSelected = { viewModel.onLanguageSelected(it) }
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
