package com.ai.keyboard.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.ai.keyboard.domain.model.KeyboardTheme

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

data class KeyboardColors(
    val background: Color,
    val keyBackground: Color,
    val keyText: Color,
    val specialKeyBackground: Color,
    val specialKeyText: Color,
    val suggestionBackground: Color,
    val suggestionText: Color
)

val LocalKeyboardColors = staticCompositionLocalOf {
    lightKeyboardColors()
}

fun lightKeyboardColors() = KeyboardColors(
    background = LightBackground,
    keyBackground = LightKeyBackground,
    keyText = LightKeyText,
    specialKeyBackground = LightSpecialKeyBackground,
    specialKeyText = LightSpecialKeyText,
    suggestionBackground = LightSuggestionBackground,
    suggestionText = LightSuggestionText
)

fun darkKeyboardColors() = KeyboardColors(
    background = DarkBackground,
    keyBackground = DarkKeyBackground,
    keyText = DarkKeyText,
    specialKeyBackground = DarkSpecialKeyBackground,
    specialKeyText = DarkSpecialKeyText,
    suggestionBackground = DarkSuggestionBackground,
    suggestionText = DarkSuggestionText
)

fun blueKeyboardColors() = KeyboardColors(
    background = BlueBackground,
    keyBackground = BlueKeyBackground,
    keyText = BlueKeyText,
    specialKeyBackground = BlueSpecialKeyBackground,
    specialKeyText = BlueSpecialKeyText,
    suggestionBackground = BlueSuggestionBackground,
    suggestionText = BlueSuggestionText
)

fun purpleKeyboardColors() = KeyboardColors(
    background = PurpleBackground,
    keyBackground = PurpleKeyBackground,
    keyText = PurpleKeyText,
    specialKeyBackground = PurpleSpecialKeyBackground,
    specialKeyText = PurpleSpecialKeyText,
    suggestionBackground = PurpleSuggestionBackground,
    suggestionText = PurpleSuggestionText
)

fun greenKeyboardColors() = KeyboardColors(
    background = GreenBackground,
    keyBackground = GreenKeyBackground,
    keyText = GreenKeyText,
    specialKeyBackground = GreenSpecialKeyBackground,
    specialKeyText = GreenSpecialKeyText,
    suggestionBackground = GreenSuggestionBackground,
    suggestionText = GreenSuggestionText
)

fun orangeKeyboardColors() = KeyboardColors(
    background = OrangeBackground,
    keyBackground = OrangeKeyBackground,
    keyText = OrangeKeyText,
    specialKeyBackground = OrangeSpecialKeyBackground,
    specialKeyText = OrangeSpecialKeyText,
    suggestionBackground = OrangeSuggestionBackground,
    suggestionText = OrangeSuggestionText
)

fun pinkKeyboardColors() = KeyboardColors(
    background = PinkBackground,
    keyBackground = PinkKeyBackground,
    keyText = PinkKeyText,
    specialKeyBackground = PinkSpecialKeyBackground,
    specialKeyText = PinkSpecialKeyText,
    suggestionBackground = PinkSuggestionBackground,
    suggestionText = PinkSuggestionText
)

fun tealKeyboardColors() = KeyboardColors(
    background = TealBackground,
    keyBackground = TealKeyBackground,
    keyText = TealKeyText,
    specialKeyBackground = TealSpecialKeyBackground,
    specialKeyText = TealSpecialKeyText,
    suggestionBackground = TealSuggestionBackground,
    suggestionText = TealSuggestionText
)

fun grayKeyboardColors() = KeyboardColors(
    background = GrayBackground,
    keyBackground = GrayKeyBackground,
    keyText = GrayKeyText,
    specialKeyBackground = GraySpecialKeyBackground,
    specialKeyText = GraySpecialKeyText,
    suggestionBackground = GraySuggestionBackground,
    suggestionText = GraySuggestionText
)

fun redKeyboardColors() = KeyboardColors(
    background = RedBackground,
    keyBackground = RedKeyBackground,
    keyText = RedKeyText,
    specialKeyBackground = RedSpecialKeyBackground,
    specialKeyText = RedSpecialKeyText,
    suggestionBackground = RedSuggestionBackground,
    suggestionText = RedSuggestionText
)

@Composable
fun AIKeyboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    keyboardTheme: KeyboardTheme = KeyboardTheme.Light,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val colors = when (keyboardTheme) {
        KeyboardTheme.Light -> lightKeyboardColors()
        KeyboardTheme.Dark -> darkKeyboardColors()
        KeyboardTheme.Blue -> blueKeyboardColors()
        KeyboardTheme.Purple -> purpleKeyboardColors()
        KeyboardTheme.Green -> greenKeyboardColors()
        KeyboardTheme.Orange -> orangeKeyboardColors()
        KeyboardTheme.Pink -> pinkKeyboardColors()
        KeyboardTheme.Teal -> tealKeyboardColors()
        KeyboardTheme.Gray -> grayKeyboardColors()
        KeyboardTheme.Red -> redKeyboardColors()
    }

    CompositionLocalProvider(LocalKeyboardColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object AIKeyboardTheme {
    val colors: KeyboardColors
        @Composable
        get() = LocalKeyboardColors.current
}