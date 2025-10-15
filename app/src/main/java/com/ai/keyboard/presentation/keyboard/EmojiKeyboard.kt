package com.ai.keyboard.presentation.keyboard
import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.domain.model.KeyAction
import com.ai.keyboard.presentation.components.BackspaceKey
import com.ai.keyboard.presentation.components.SpecialKeyButton
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent
import com.ai.keyboard.presentation.theme.AIKeyboardTheme
import kotlinx.coroutines.launch

data class EmojiCategory(
    val icon: String,
    val name: String,
    val emojis: List<String>
)

val defaultEmojiCategories = listOf(
    EmojiCategory(
        icon = "😀",
        name = "Smileys & People",
        emojis = listOf("😀", "😂", "🥰", "😍", "🤩", "🤔", "😘", "🥺", "😭", "🙏", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "😘", "😗", "😙")
    ),
    EmojiCategory(
        icon = "🐻",
        name = "Animals & Nature",
        emojis = listOf("🐶", "🐱", "🐭", "🐹", "🐻", "🐼", "🦁", "🐒", "🦍", "🦊", "🌸", "🌹", "🌺", "🍁", "☀️", "🌙", "⭐", "⚡", "☔", "🌈")
    ),
    EmojiCategory(
        icon = "🍔",
        name = "Food & Drink",
        emojis = listOf("🍕", "🍔", "🍟", "🍣", "🍦", "☕", "🍺", "🍎", "🍓", "🍉", "🍪", "🍩", "🍫", "🍿", "🍳", "🍜", "🍚", "🍲", "🍇", "🍊")
    ),
    EmojiCategory(
        icon = "⚽",
        name = "Activity",
        emojis = listOf("⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🎮", "🎹", "🎤", "🎬", "🎨", "🎭", "🚀", "⛵", "🚗", "🚂", "✈️", "🚴", "🏊", "🏋️")
    ),
    EmojiCategory(
        icon = "💡",
        name = "Objects",
        emojis = listOf("📱", "💻", "💡", "📚", "💰", "🎁", "🎉", "🎈", "⏱️", "📌", "🔑", "🔨", "✂️", "🔬", "💊", "🚽", "🚿", "🛁", "🚬", "💣")
    ),
    EmojiCategory(
        icon = "🛑",
        name = "Symbols",
        emojis = listOf("❤️", "🔥", "✨", "⭐", "✅", "⚠️", "❌", "💯", "🎶", "🌐", "☮️", "✝️", "☪️", "🕉️", "✡️", "☯️", "☸️", "♈", "♉", "♊")
    ),
    EmojiCategory(
        icon = "😀",
        name = "Smileys & People",
        emojis = listOf("😀", "😂", "🥰", "😍", "🤩", "🤔", "😘", "🥺", "😭", "🙏", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "😘", "😗", "😙")
    ),
    EmojiCategory(
        icon = "🐻",
        name = "Animals & Nature",
        emojis = listOf("🐶", "🐱", "🐭", "🐹", "🐻", "🐼", "🦁", "🐒", "🦍", "🦊", "🌸", "🌹", "🌺", "🍁", "☀️", "🌙", "⭐", "⚡", "☔", "🌈")
    ),
    EmojiCategory(
        icon = "🍔",
        name = "Food & Drink",
        emojis = listOf("🍕", "🍔", "🍟", "🍣", "🍦", "☕", "🍺", "🍎", "🍓", "🍉", "🍪", "🍩", "🍫", "🍿", "🍳", "🍜", "🍚", "🍲", "🍇", "🍊")
    ),
    EmojiCategory(
        icon = "⚽",
        name = "Activity",
        emojis = listOf("⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🎮", "🎹", "🎤", "🎬", "🎨", "🎭", "🚀", "⛵", "🚗", "🚂", "✈️", "🚴", "🏊", "🏋️")
    ),
    EmojiCategory(
        icon = "💡",
        name = "Objects",
        emojis = listOf("📱", "💻", "💡", "📚", "💰", "🎁", "🎉", "🎈", "⏱️", "📌", "🔑", "🔨", "✂️", "🔬", "💊", "🚽", "🚿", "🛁", "🚬", "💣")
    ),
    EmojiCategory(
        icon = "🛑",
        name = "Symbols",
        emojis = listOf("❤️", "🔥", "✨", "⭐", "✅", "⚠️", "❌", "💯", "🎶", "🌐", "☮️", "✝️", "☪️", "🕉️", "✡️", "☯️", "☸️", "♈", "♉", "♊")
    ),
    EmojiCategory(
        icon = "😀",
        name = "Smileys & People",
        emojis = listOf("😀", "😂", "🥰", "😍", "🤩", "🤔", "😘", "🥺", "😭", "🙏", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "😘", "😗", "😙")
    ),
    EmojiCategory(
        icon = "🐻",
        name = "Animals & Nature",
        emojis = listOf("🐶", "🐱", "🐭", "🐹", "🐻", "🐼", "🦁", "🐒", "🦍", "🦊", "🌸", "🌹", "🌺", "🍁", "☀️", "🌙", "⭐", "⚡", "☔", "🌈")
    ),
    EmojiCategory(
        icon = "🍔",
        name = "Food & Drink",
        emojis = listOf("🍕", "🍔", "🍟", "🍣", "🍦", "☕", "🍺", "🍎", "🍓", "🍉", "🍪", "🍩", "🍫", "🍿", "🍳", "🍜", "🍚", "🍲", "🍇", "🍊")
    ),
    EmojiCategory(
        icon = "⚽",
        name = "Activity",
        emojis = listOf("⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🎮", "🎹", "🎤", "🎬", "🎨", "🎭", "🚀", "⛵", "🚗", "🚂", "✈️", "🚴", "🏊", "🏋️")
    ),
    EmojiCategory(
        icon = "💡",
        name = "Objects",
        emojis = listOf("📱", "💻", "💡", "📚", "💰", "🎁", "🎉", "🎈", "⏱️", "📌", "🔑", "🔨", "✂️", "🔬", "💊", "🚽", "🚿", "🛁", "🚬", "💣")
    ),
    EmojiCategory(
        icon = "🛑",
        name = "Symbols",
        emojis = listOf("❤️", "🔥", "✨", "⭐", "✅", "⚠️", "❌", "💯", "🎶", "🌐", "☮️", "✝️", "☪️", "🕉️", "🔥", "✨", "⭐", "✅", "⚠️", "❌", "🔥", "✨", "⭐", "✅", "⚠️", "❌", "💯", "🎶", "🌐", "☮️", "✝️", "☪️", "🕉️",  "💯", "🎶", "🌐", "☮️", "✝️", "☪️", "🕉️", "✡️", "☯️", "☸️", "♈", "♉", "♊")
    )
)

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiKeyboard(
    onIntent: (KeyboardIntent) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<EmojiCategory> = defaultEmojiCategories
) {
    val listState = rememberLazyListState()
    val tabListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val emojiCellSize = 40.dp
    val tabWidth = 45.dp
    val indicatorHeight = 3.dp

    val firstVisibleCategoryIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    LaunchedEffect(firstVisibleCategoryIndex) {
        coroutineScope.launch {
            tabListState.animateScrollToItem(
                index = firstVisibleCategoryIndex.coerceIn(0, categories.lastIndex)
            )
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {

        LazyRow(
            state = tabListState,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(AIKeyboardTheme.colors.specialKeyBackground),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            itemsIndexed(categories) { index, category ->
                val isSelected = firstVisibleCategoryIndex == index
                Column(
                    modifier = Modifier
                        .width(tabWidth)
                        .fillMaxHeight()
                        .clickable {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        category.icon,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = if (isSelected) 1f else 0.4f
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(indicatorHeight)
                            .background(
                                if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Transparent
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            itemsIndexed(categories) { _, category ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = category.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AIKeyboardTheme.colors.keyText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        category.emojis.forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(emojiCellSize)
                                    .clickable {
                                        onIntent(KeyboardIntent.KeyPressed(KeyAction.Character(emoji)))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = emoji,
                                    fontSize = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpecialKeyButton(
                icon = "ABC",
                onClick = { onIntent(KeyboardIntent.AlphabetPressed) },
                modifier = Modifier.weight(1.5f)
            )

            Spacer(modifier = Modifier.weight(4f))

            BackspaceKey(
                modifier = Modifier.weight(1.5f),
                text = "⌫",
                onClick = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Backspace)) },
                onRepeat = { onIntent(KeyboardIntent.KeyPressed(KeyAction.Backspace)) },
                onSelectionSwipe = { amount ->
                    onIntent(KeyboardIntent.KeyPressed(KeyAction.SelectAndDelete(amount)))
                }
            )
        }
    }
}