package com.ai.keyboard.presentation.screen.option

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
fun FixGrammarScreen(
    onBackButtonClick: () -> Unit,
) {
    FixGrammarScreenContent(
        onBackButtonClick = onBackButtonClick,
    )
}

@Composable
fun FixGrammarScreenContent(
    onBackButtonClick: () -> Unit,
) {

    Column {


        val actions = listOf(
            ActionButtonData(label = "Rephrase", icon = R.drawable.ic_magic),
            ActionButtonData(label = "Grammar Fix", emoji = "🛠️"),
            ActionButtonData(label = "Add emoji", emoji = "🤗")
        )
        var selectedLanguage by remember { mutableStateOf("English") }
        CustomToolbar(
            title = stringResource(R.string.fix_grammar),
            onBackButtonClicked = onBackButtonClick,
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it }
        )
        ActionButtonRow(
            actions = actions,
            onActionClick = { println("Clicked: ${it.label}") }
        )
        Box(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            ContentCard(
                text = "Hi John, what?",
                buttonText = "Replace Text",
                onButtonClick = { /* action here */ }
            )
        }
        CustomBottomBar(
            onBackButtonClick = onBackButtonClick,
        )

    }

}

