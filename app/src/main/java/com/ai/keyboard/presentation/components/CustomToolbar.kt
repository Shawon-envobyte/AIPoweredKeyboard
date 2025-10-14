package com.ai.keyboard.presentation.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ai.keyboard.R
import com.ai.keyboard.presentation.model.LanguageType
import com.ai.keyboard.presentation.theme.DropdownGradient

@Composable
fun CustomToolbar(
    modifier: Modifier = Modifier,
    title: String = "Fix Grammar",
    onBackButtonClicked: () -> Unit,
    languages: List<LanguageType> = LanguageType.values().toList(),
    selectedLanguage: LanguageType = LanguageType.ENGLISH,
    onLanguageSelected: (LanguageType) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var anchorPosition by remember { mutableStateOf(IntOffset.Zero) }
    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Back icon + title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackButtonClicked() }
                )
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Gradient button with anchor position tracking
            Row(
                modifier = Modifier
                    .onGloballyPositioned {
                        val position = it.localToWindow(Offset.Zero)
                        anchorPosition = IntOffset(
                            position.x.toInt(),
                            (position.y + it.size.height).toInt()
                        )
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)

            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_language),
                    contentDescription = "Expand",
                    modifier = Modifier.size(30.dp)
                )
                Row(
                    modifier = modifier
                        .clip(RoundedCornerShape(50))
                        .border(
                            width = 1.dp,
                            brush = DropdownGradient,
                            shape = RoundedCornerShape(50)
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { expanded = !expanded }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(selectedLanguage.displayName, style = MaterialTheme.typography.bodyMedium)
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = with(density) { IntOffset(anchorPosition.x, anchorPosition.y) },
                onDismissRequest = { expanded = false },
                properties = PopupProperties(
                    focusable = false, // 👈 prevents IME from closing
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true
                )
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .width(160.dp)
                ) {
                    languages.forEach { language ->
                        Text(
                            text = language.displayName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expanded = false
                                    onLanguageSelected(language)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GrammarToolbarPreview() {
    var selectedLanguage by remember { mutableStateOf(LanguageType.ENGLISH) }

    MaterialTheme {
        CustomToolbar(

            onBackButtonClicked = {},
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it }
        )
    }
}
