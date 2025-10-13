package com.ai.keyboard.presentation.screen.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.presentation.components.SuggestionBar
import com.ai.keyboard.presentation.keyboard.AlphabeticKeyboard
import com.ai.keyboard.presentation.keyboard.ExtendedSymbolKeyboard
import com.ai.keyboard.presentation.keyboard.SymbolKeyboard
import com.ai.keyboard.presentation.theme.AIKeyboardTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun KeyboardScreen(
    onTextChange: (String, Int) -> Unit,
    onCursorChange: (Int) -> Unit,
    onKeyPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KeyboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setOnTextChangeListener(onTextChange)
        viewModel.setOnCursorChangeListener(onCursorChange)
        viewModel.setOnKeyPressListener(onKeyPress)
    }

    AIKeyboardTheme(keyboardTheme = uiState.keyboardState.theme) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(AIKeyboardTheme.colors.background)
                .padding(vertical = 4.dp, horizontal = 2.dp)
                .navigationBarsPadding()
        ) {
            // Suggestion Bar
            SuggestionBar(
                suggestions = uiState.suggestions,
                onSuggestionClick = { suggestion ->
                    viewModel.handleIntent(
                        KeyboardIntent.SuggestionSelected(suggestion.text)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Keyboard Layout
            when (uiState.keyboardState.mode) {
                KeyboardMode.SYMBOLS -> {
                    SymbolKeyboard(
                        onIntent = { viewModel.handleIntent(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                KeyboardMode.EXTENDED_SYMBOLS -> {
                    ExtendedSymbolKeyboard(
                        onIntent = { viewModel.handleIntent(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    AlphabeticKeyboard(
                        mode = uiState.keyboardState.mode,
                        onIntent = { viewModel.handleIntent(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}