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
import com.ai.keyboard.presentation.components.ActionButtonData
import com.ai.keyboard.presentation.components.ActionButtonRow
import com.ai.keyboard.presentation.components.ContentCard
import com.ai.keyboard.presentation.components.CustomBottomBar
import com.ai.keyboard.presentation.components.CustomToolbar

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


        val actions = listOf(
            ActionButtonData(label = "Rephrase", icon = R.drawable.ic_magic),
            ActionButtonData(label = "Grammar Fix", emoji = "🛠️"),
            ActionButtonData(label = "Add emoji", emoji = "🤗")
        )
        var selectedLanguage by remember { mutableStateOf("English") }
        CustomToolbar(
            title = stringResource(R.string.word_tone),
            onBackButtonClicked = onBackButtonClick,
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it }
        )
        ActionButtonRow(
            actions = actions,
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
