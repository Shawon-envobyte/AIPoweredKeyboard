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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ai.keyboard.domain.model.KeyboardMode
import com.ai.keyboard.presentation.components.OptionBar
import com.ai.keyboard.presentation.components.SuggestionBar
import com.ai.keyboard.presentation.keyboard.AlphabeticKeyboard
import com.ai.keyboard.presentation.keyboard.EmojiKeyboard
import com.ai.keyboard.presentation.keyboard.ExtendedSymbolKeyboard
import com.ai.keyboard.presentation.keyboard.SymbolKeyboard
import com.ai.keyboard.presentation.screen.ai_assistance.AIWritingAssistanceScreen
import com.ai.keyboard.presentation.screen.fix_grammar.FixGrammarScreen
import com.ai.keyboard.presentation.screen.quick_reply.QuickReplyScreen
import com.ai.keyboard.presentation.screen.translate.TranslateScreen
import com.ai.keyboard.presentation.screen.word_tone.WordToneScreen
import com.ai.keyboard.presentation.theme.AIKeyboardTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun KeyboardScreen(
    onTextChange: (String, Int) -> Unit,
    onCursorChange: (Int) -> Unit,
    onTextSelectAndDelete: (Int) -> Unit,
    onKeyPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KeyboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Optimize recomposition
    val keyboardMode by remember {
        derivedStateOf { uiState.keyboardState.mode }
    }

    val suggestions by remember {
        derivedStateOf { uiState.suggestions }
    }

    val isNumberRowEnabled by remember {
        derivedStateOf { uiState.keyboardState.isNumberRowEnabled }
    }

    LaunchedEffect(Unit) {
        viewModel.setOnTextChangeListener(onTextChange)
        viewModel.setOnCursorChangeListener(onCursorChange)
        viewModel.setOnTextSelectAndDeleteListener(onTextSelectAndDelete)
        viewModel.setOnKeyPressListener(onKeyPress)
    }

    AIKeyboardTheme(keyboardTheme = uiState.keyboardState.theme) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(AIKeyboardTheme.colors.background)
                .padding(vertical = 4.dp, horizontal = 6.dp)
                .navigationBarsPadding()
        ) {
            // Suggestion Bar

            OptionBar(
                onGrammarClick = { viewModel.handleIntent(KeyboardIntent.FixGrammarPressed) },
                onMagicClick = { viewModel.handleIntent(KeyboardIntent.RewritePressed) },
                onTranslateClick = { viewModel.handleIntent(KeyboardIntent.TranslatePressed) },
                onChatClick = { viewModel.handleIntent(KeyboardIntent.GetQuickReply) },
                onClipboardClick = { viewModel.handleIntent(KeyboardIntent.AiAssistancePressed) },
                onEmojiClick = { viewModel.handleIntent(KeyboardIntent.EmojiPressed) },
                onDotClick = {},
                onMicClick = { 
                    viewModel.handleIntent(KeyboardIntent.VoiceToTextPressed)
                },
                isListening = uiState.isListening
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Suggestion Bar - only recompose when suggestions change
            SuggestionBar(
                suggestions = suggestions,
                onSuggestionClick = { suggestion ->
                    viewModel.handleIntent(
                        KeyboardIntent.SuggestionSelected(suggestion.text)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Keyboard Layout
            when (keyboardMode) {
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

                KeyboardMode.FIX_GRAMMAR -> {
                    FixGrammarScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        onBackButtonClick = { viewModel.handleIntent(KeyboardIntent.AlphabetPressed) },
                    )
                }

                KeyboardMode.EMOJI -> {
                    EmojiKeyboard(
                        onIntent = { viewModel.handleIntent(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                KeyboardMode.REWRITE -> {
                    WordToneScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        onBackButtonClick = { viewModel.handleIntent(KeyboardIntent.AlphabetPressed) },
                    )
                }

                KeyboardMode.AI_ASSISTANCE -> {
                    AIWritingAssistanceScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        onBackButtonClick = { viewModel.handleIntent(KeyboardIntent.AlphabetPressed) },
                    )
                }

                KeyboardMode.TRANSLATE -> {
                    TranslateScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        onBackButtonClick = { viewModel.handleIntent(KeyboardIntent.AlphabetPressed) },
                    )
                }

                KeyboardMode.QUICK_REPLY -> {
                    QuickReplyScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        onBackButtonClick = { viewModel.handleIntent(KeyboardIntent.AlphabetPressed) },
                    )
                }

                else -> {
                    AlphabeticKeyboard(
                        mode = keyboardMode,
                        isNumberRowEnabled = isNumberRowEnabled,
                        onIntent = { viewModel.handleIntent(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
