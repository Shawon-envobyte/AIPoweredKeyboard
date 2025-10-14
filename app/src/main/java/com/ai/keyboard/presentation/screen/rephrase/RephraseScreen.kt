package com.ai.keyboard.presentation.screen.rephrase

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ai.keyboard.R
import com.ai.keyboard.presentation.components.ActionButtonRow
import com.ai.keyboard.presentation.components.ContentCard
import com.ai.keyboard.presentation.components.CustomBottomBar
import com.ai.keyboard.presentation.components.CustomToolbar
import com.ai.keyboard.presentation.model.ActionButtonType
import com.ai.keyboard.presentation.model.LanguageType

@Composable
fun RephraseScreen(
    onBackButtonClick: () -> Unit,
) {
    RephraseScreenContent(
        onBackButtonClick = onBackButtonClick,
    )
}

@Composable
fun RephraseScreenContent(
    onBackButtonClick: () -> Unit,
) {

    Column(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {



        var selectedLanguage by remember { mutableStateOf(LanguageType.ENGLISH) }
        CustomToolbar(
            title = stringResource(R.string.word_tone),
            onBackButtonClicked = onBackButtonClick,
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it  }
        )
        ActionButtonRow(
            actions = ActionButtonType.values().map { it },
            onActionClick = { println("Clicked: ${it.label}") }
        )
        ContentCard(
            showButton = false,
            text = "Hi John, what?",
            buttonText = "Replace Text",
            onButtonClick = { /* action here */ }
        )
        Spacer(modifier = Modifier.height(2.dp))
        ContentCard(
            showButton = false,
            text = "Hi John, what?",
            buttonText = "Replace Text",
            onButtonClick = { /* action here */ }
        )
        Spacer(modifier = Modifier.height(2.dp))
        ContentCard(
            showButton = false,
            text = "Hi John, what?",
            buttonText = "Replace Text",
            onButtonClick = { /* action here */ }
        )
        CustomBottomBar(
            onBackButtonClick = onBackButtonClick,
        )

    }

}
