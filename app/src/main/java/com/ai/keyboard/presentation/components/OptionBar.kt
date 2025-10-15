package com.ai.keyboard.presentation.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ai.keyboard.R
import com.ai.keyboard.presentation.theme.AIKeyboardTheme

@Composable
fun OptionBar(
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onGrammarClick: () -> Unit,
    onMagicClick: () -> Unit,
    onTranslateClick: () -> Unit,
    onChatClick: () -> Unit,
    onClipboardClick: () -> Unit,
    onEmojiClick: () -> Unit,
    onDotClick: () -> Unit,
    onMicClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        OptionBarButtonIcon(onClick = onGrammarClick, icon = R.drawable.ic_grammar)
        OptionBarButtonIcon(onClick = onMagicClick, icon = R.drawable.ic_magic)
        OptionBarButtonIcon(onClick = onTranslateClick, icon = R.drawable.ic_translate)
        OptionBarButtonIcon(onClick = onChatClick, icon = R.drawable.ic_chat)
        OptionBarButtonIcon(onClick = onClipboardClick, icon = R.drawable.ic_clipboard)
        OptionBarIconButton(onClick = onEmojiClick, icon = R.drawable.ic_emoji)
        OptionBarIconButton(onClick = onUndoClick, icon = R.drawable.ic_undo_baseline)
        OptionBarIconButton(onClick = onRedoClick, icon = R.drawable.ic_redo_outline)
        OptionBarButtonIcon(onClick = onDotClick, icon = R.drawable.ic_dot)
        OptionBarButtonIcon(onClick = onMicClick, icon = R.drawable.ic_mic)
    }


}


@Composable
fun OptionBarIconButton(onClick: () -> Unit, icon: Int) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = onClick)
    )
    {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = AIKeyboardTheme.colors.keyText
        )
    }
}

@Composable
fun OptionBarButtonIcon(onClick: () -> Unit, icon: Int) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = onClick)
    )
    {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OptionBar(
        onUndoClick = {},
        onRedoClick = {},
        onGrammarClick = {},
        onMagicClick = {},
        onTranslateClick = {},
        onChatClick = {},
        onClipboardClick = {},
        onEmojiClick = {},
        onDotClick = {},
        onMicClick = {}
    )
}