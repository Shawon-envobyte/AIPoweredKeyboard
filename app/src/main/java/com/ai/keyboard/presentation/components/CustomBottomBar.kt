package com.ai.keyboard.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ai.keyboard.R

@Composable
fun CustomBottomBar(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {


        Image(
            painter = painterResource(id = R.drawable.ic_keyboard),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onBackButtonClick()
            },
            contentDescription = "Keyboard",
        )

    }
}

@Preview(showBackground = true)
@Composable
fun KeyboardBottomBarPreview() {
    CustomBottomBar(
        onBackButtonClick = {}
    )
}
