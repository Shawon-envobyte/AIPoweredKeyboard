package com.ai.keyboard.presentation.screen.keyboard

import com.ai.keyboard.domain.model.KeyboardState
import com.ai.keyboard.domain.model.QuickReply
import com.ai.keyboard.domain.model.Suggestion
import com.ai.keyboard.presentation.model.AIWritingAssistanceType
import com.ai.keyboard.presentation.model.ActionButtonType
import com.ai.keyboard.presentation.model.LanguageType
import com.ai.keyboard.presentation.model.QuickReplyModule
import com.ai.keyboard.presentation.model.WordToneType

data class KeyboardUIState(
    val keyboardState: KeyboardState = KeyboardState(),
    val suggestions: List<Suggestion> = emptyList(),
    val quickReplyList: QuickReply = QuickReply(
        positive = listOf(".....", ".....", "....."),
        neutral = listOf(".....", ".....", "....."),
        negative = listOf(".....", ".....", ".....")
    ),
    val isLoading: Boolean = false,
    val error: String? = null,
    val correctedText: String = "",
    val language: LanguageType = LanguageType.ENGLISH,
    val selectedAction: ActionButtonType = ActionButtonType.REPHRASE,
    val selectedAiAction: AIWritingAssistanceType = AIWritingAssistanceType.CHATGPT,
    val selectedWordAction: WordToneType = WordToneType.REWRITE,
    val selectQuickReplyAction: QuickReplyModule = QuickReplyModule.POSITIVE,
    val inputFieldText: String = "",
    val isListening: Boolean = false,
    val voiceToTextResult: String = "",
    val needsAudioPermission: Boolean = false
)