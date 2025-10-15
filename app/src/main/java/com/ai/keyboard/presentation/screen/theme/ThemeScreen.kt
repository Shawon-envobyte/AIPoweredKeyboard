package com.ai.keyboard.presentation.screen.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.keyboard.domain.model.KeyboardTheme
import com.ai.keyboard.presentation.theme.BlueBackground
import com.ai.keyboard.presentation.theme.DarkBackground
import com.ai.keyboard.presentation.theme.GrayBackground
import com.ai.keyboard.presentation.theme.GreenBackground
import com.ai.keyboard.presentation.theme.LightBackground
import com.ai.keyboard.presentation.theme.OrangeBackground
import com.ai.keyboard.presentation.theme.PinkBackground
import com.ai.keyboard.presentation.theme.PurpleBackground
import com.ai.keyboard.presentation.theme.RedBackground
import com.ai.keyboard.presentation.theme.TealBackground

@Composable
fun ThemeScreen(
    currentTheme: KeyboardTheme,
    onThemeChange: (KeyboardTheme) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Choose Your Theme",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        KeyboardTheme.entries.forEach { theme ->
            ThemePreviewCard(
                theme = theme,
                isSelected = theme == currentTheme,
                onClick = { onThemeChange(theme) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ThemePreviewCard(
    theme: KeyboardTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundBrush = when (theme) {
        KeyboardTheme.Light -> SolidColor(LightBackground)
        KeyboardTheme.Dark -> SolidColor(DarkBackground)
        KeyboardTheme.Blue -> SolidColor(BlueBackground)
        KeyboardTheme.Purple -> SolidColor(PurpleBackground)
        KeyboardTheme.Pink -> SolidColor(PinkBackground)
        KeyboardTheme.Green -> SolidColor(GreenBackground)
        KeyboardTheme.Orange -> SolidColor(OrangeBackground)
        KeyboardTheme.Red -> SolidColor(RedBackground)
        KeyboardTheme.Teal -> SolidColor(TealBackground)
        KeyboardTheme.Gray -> SolidColor(GrayBackground)

        // 🌅 Gradient Themes
        KeyboardTheme.SunsetGradient -> Brush.linearGradient(
            listOf(
                Color(0xFFFF5F6D), // vivid coral-red
                Color(0xFFFFC371)  // warm orange-gold
            )
        )

        KeyboardTheme.OceanGradient -> Brush.linearGradient(
            listOf(
                Color(0xFF36D1DC), // cyan-blue
                Color(0xFF5B86E5)  // deep ocean blue
            )
        )

        KeyboardTheme.ForestGradient -> Brush.linearGradient(
            listOf(
                Color(0xFF11998E), // jungle green
                Color(0xFF38EF7D)  // soft lime green
            )
        )

        KeyboardTheme.LavenderGradient -> Brush.linearGradient(
            listOf(
                Color(0xFFB993D6), // rich lavender
                Color(0xFF8CA6DB)  // gentle periwinkle
            )
        )

        KeyboardTheme.RoseGradient -> Brush.linearGradient(
            listOf(
                Color(0xFFF857A6), // pink rose
                Color(0xFFFF5858)  // bright coral red
            )
        )

        KeyboardTheme.CitrusGradient -> Brush.linearGradient(
            listOf(
                Color(0xFFFDC830), // golden yellow
                Color(0xFFF37335)  // orange-red zest
            )
        )

        KeyboardTheme.SkyGradient -> Brush.linearGradient(
            listOf(
                Color(0xFF00C9FF), // bright cyan
                Color(0xFF92FE9D)  // minty green
            )
        )

        KeyboardTheme.MintGradient -> Brush.linearGradient(
            listOf(
                Color(0xFF00B09B), // teal mint
                Color(0xFF96C93D)  // lime green
            )
        )

        KeyboardTheme.CoralGradient -> Brush.linearGradient(
            listOf(
                Color(0xFFFF9966), // coral orange
                Color(0xFFFF5E62)  // rose red
            )
        )

        KeyboardTheme.ElectricNeonGradient -> Brush.linearGradient(
            listOf(
                Color(0xFFFF007F),
                Color(0xFF00FFFF)
            )
        )
    }

    val textColor = if (theme == KeyboardTheme.Light) Color.Black else Color.White

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        border = if (isSelected) {
            BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
        } else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theme Preview
            Surface(
                modifier = Modifier.size(80.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Box(
                    modifier = Modifier.background(backgroundBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aa",
                        color = textColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when (theme) {
                        KeyboardTheme.Light -> "Clean and minimal"
                        KeyboardTheme.Dark -> "Easy on the eyes"
                        KeyboardTheme.Blue -> "Professional look"
                        KeyboardTheme.Purple -> "Vibrant and modern"
                        KeyboardTheme.Pink -> "Soft and feminine"
                        KeyboardTheme.Green -> "Fruity and refreshing"
                        KeyboardTheme.Orange -> "Warm and inviting"
                        KeyboardTheme.Red -> "Bold and attention-grabbing"
                        KeyboardTheme.Teal -> "Calm and refreshing"
                        KeyboardTheme.Gray -> "Muted and neutral"
                        KeyboardTheme.MintGradient -> "Mint to turquoise"
                        KeyboardTheme.SunsetGradient -> "Soft pink to peach sunset"
                        KeyboardTheme.OceanGradient -> "Aqua to blue ocean"
                        KeyboardTheme.ForestGradient -> "Green to misty forest"
                        KeyboardTheme.LavenderGradient -> "Soft lavender to sky blue"
                        KeyboardTheme.RoseGradient -> "Pink to periwinkle"
                        KeyboardTheme.CitrusGradient -> "Yellow to orange"
                        KeyboardTheme.SkyGradient -> "Teal sky to blue sky"
                        KeyboardTheme.CoralGradient -> "Coral pink to rose red"
                        KeyboardTheme.ElectricNeonGradient -> "Deep gray to steel"
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}