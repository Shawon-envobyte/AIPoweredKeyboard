package com.ai.keyboard.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.SuggestionChip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ai.keyboard.domain.model.Suggestion

@Composable
fun SuggestionBar(
    suggestions: List<Suggestion>,
    onSuggestionClick: (Suggestion) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        suggestions.take(3).forEach { suggestion ->
            SuggestionChip(
                suggestion = suggestion,
                onClick = { onSuggestionClick(suggestion) },
                modifier = Modifier.weight(1f)
            )
        }

        // Fill empty slots
        repeat(3 - suggestions.size) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}