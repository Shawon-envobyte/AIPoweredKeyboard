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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
    val backgroundBrush: Brush,
    val keyBackground: Color,
    val keyText: Color,
    val specialKeyBackground: Color,
    val specialKeyText: Color,
    val suggestionBackground: Color,
    val suggestionText: Color,
    val popupBackground: Color,
    val popupText: Color
)

val LocalKeyboardColors = staticCompositionLocalOf {
    lightKeyboardColors()
}

fun lightKeyboardColors() = KeyboardColors(
    backgroundBrush = SolidColor(LightBackground),
    keyBackground = LightKeyBackground,
    keyText = LightKeyText,
    specialKeyBackground = LightSpecialKeyBackground,
    specialKeyText = LightSpecialKeyText,
    suggestionBackground = LightSuggestionBackground,
    suggestionText = LightSuggestionText,
    popupBackground = Color(0xFFFFFFFF),
    popupText = Color(0xFF000000)
)

fun darkKeyboardColors() = KeyboardColors(
    backgroundBrush = SolidColor(Color(0xFF1A1A1D)),
    keyBackground = Color(0xFF2A2A2D),
    keyText = Color(0xFFFFFFFF),
    specialKeyBackground = Color(0xFF3A3A3D),
    specialKeyText = Color(0xFFFFFFFF),
    suggestionBackground = Color(0xFF2E2E32),
    suggestionText = Color(0xFFEDEDED),
    popupBackground = Color(0xFF38383C),
    popupText = Color(0xFFFFFFFF)
)

fun blueKeyboardColors() = KeyboardColors(
    backgroundBrush = SolidColor(Color(0xFFE3F2FD)),
    keyBackground = Color(0xFFBBDEFB),
    keyText = Color(0xFF0D47A1),
    specialKeyBackground = Color(0xFF1976D2),
    specialKeyText = Color.White,
    suggestionBackground = Color(0xFF90CAF9),
    suggestionText = Color(0xFF0D47A1),
    popupBackground = Color(0xFF2196F3),
    popupText = Color.White
)

fun purpleKeyboardColors() = KeyboardColors(
    backgroundBrush = SolidColor(Color(0xFFF3E5F5)),
    keyBackground = Color(0xFFE1BEE7),
    keyText = Color(0xFF4A148C),
    specialKeyBackground = Color(0xFF7B1FA2),
    specialKeyText = Color.White,
    suggestionBackground = Color(0xFFCE93D8),
    suggestionText = Color(0xFF4A148C),
    popupBackground = Color(0xFF9C27B0),
    popupText = Color.White
)

fun pinkKeyboardColors() = KeyboardColors(
    backgroundBrush = SolidColor(Color(0xFFFCE4EC)),
    keyBackground = Color(0xFFF8BBD0),
    keyText = Color(0xFF880E4F),
    specialKeyBackground = Color(0xFFC2185B),
    specialKeyText = Color.White,
    suggestionBackground = Color(0xFFF48FB1),
    suggestionText = Color(0xFF880E4F),
    popupBackground = Color(0xFFE91E63),
    popupText = Color.White
)

fun tealKeyboardColors() = KeyboardColors(
    backgroundBrush = SolidColor(Color(0xFFE0F2F1)),
    keyBackground = Color(0xFFB2DFDB),
    keyText = Color(0xFF004D40),
    specialKeyBackground = Color(0xFF00796B),
    specialKeyText = Color.White,
    suggestionBackground = Color(0xFF80CBC4),
    suggestionText = Color(0xFF004D40),
    popupBackground = Color(0xFF009688),
    popupText = Color.White
)

fun orangeKeyboardColors() = KeyboardColors(
    backgroundBrush = SolidColor(Color(0xFFFFF3E0)),
    keyBackground = Color(0xFFFFE0B2),
    keyText = Color(0xFFE65100),
    specialKeyBackground = Color(0xFFFB8C00),
    specialKeyText = Color.White,
    suggestionBackground = Color(0xFFFFCC80),
    suggestionText = Color(0xFFBF360C),
    popupBackground = Color(0xFFFF9800),
    popupText = Color.White
)

fun greenKeyboardColors() = KeyboardColors(
    backgroundBrush = SolidColor(Color(0xFFE8F5E9)),
    keyBackground = Color(0xFFC8E6C9),
    keyText = Color(0xFF1B5E20),
    specialKeyBackground = Color(0xFF388E3C),
    specialKeyText = Color.White,
    suggestionBackground = Color(0xFFA5D6A7),
    suggestionText = Color(0xFF1B5E20),
    popupBackground = Color(0xFF4CAF50),
    popupText = Color.White
)

fun redKeyboardColors() = KeyboardColors(
    backgroundBrush = SolidColor(Color(0xFFFFEBEE)),
    keyBackground = Color(0xFFFFCDD2),
    keyText = Color(0xFFB71C1C),
    specialKeyBackground = Color(0xFFD32F2F),
    specialKeyText = Color.White,
    suggestionBackground = Color(0xFFEF9A9A),
    suggestionText = Color(0xFFB71C1C),
    popupBackground = Color(0xFFF44336),
    popupText = Color.White
)

fun grayKeyboardColors() = KeyboardColors(
    backgroundBrush = SolidColor(Color(0xFFF5F5F5)),
    keyBackground = Color(0xFFEEEEEE),
    keyText = Color(0xFF212121),
    specialKeyBackground = Color(0xFF9E9E9E),
    specialKeyText = Color.White,
    suggestionBackground = Color(0xFFE0E0E0),
    suggestionText = Color(0xFF212121),
    popupBackground = Color(0xFFBDBDBD),
    popupText = Color.White
)

fun sunsetGradientKeyboardColors() = KeyboardColors(
    backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFFC3A0), Color(0xFFFFAFBD))),
    keyBackground = Color(0xFFFFC3A0),
    keyText = Color(0xFF4A3F35),
    specialKeyBackground = Color(0xFFFFAFBD),
    specialKeyText = Color(0xFF4A3F35),
    suggestionBackground = Color(0xFFFFD8C0),
    suggestionText = Color(0xFF4A3F35),
    popupBackground = Color(0xFFFFAFBD),
    popupText = Color(0xFF4A3F35)
)

fun oceanGradientKeyboardColors() = KeyboardColors(
    backgroundBrush = Brush.linearGradient(listOf(Color(0xFFA1C4FD), Color(0xFFC2E9FB))),
    keyBackground = Color(0xFFA1C4FD),
    keyText = Color(0xFF1A1A2E),
    specialKeyBackground = Color(0xFFC2E9FB),
    specialKeyText = Color(0xFF1A1A2E),
    suggestionBackground = Color(0xFFB7DFFF),
    suggestionText = Color(0xFF1A1A2E),
    popupBackground = Color(0xFFC2E9FB),
    popupText = Color(0xFF1A1A2E)
)

fun forestGradientKeyboardColors() = KeyboardColors(
    backgroundBrush = Brush.linearGradient(listOf(Color(0xFFA8E6CF), Color(0xFFDCEFCF))),
    keyBackground = Color(0xFFA8E6CF),
    keyText = Color(0xFF1B3A2F),
    specialKeyBackground = Color(0xFFDCEFCF),
    specialKeyText = Color(0xFF1B3A2F),
    suggestionBackground = Color(0xFFCFF2E3),
    suggestionText = Color(0xFF1B3A2F),
    popupBackground = Color(0xFFDCEFCF),
    popupText = Color(0xFF1B3A2F)
)

fun lavenderGradientKeyboardColors() = KeyboardColors(
    backgroundBrush = Brush.linearGradient(listOf(Color(0xFFD9AFD9), Color(0xFF97D9E1))),
    keyBackground = Color(0xFFD9AFD9),
    keyText = Color(0xFF3B1F3B),
    specialKeyBackground = Color(0xFF97D9E1),
    specialKeyText = Color(0xFF3B1F3B),
    suggestionBackground = Color(0xFFC9C3E5),
    suggestionText = Color(0xFF3B1F3B),
    popupBackground = Color(0xFF97D9E1),
    popupText = Color(0xFF3B1F3B)
)

fun roseGradientKeyboardColors() = KeyboardColors(
    backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFFC1E3), Color(0xFFFFA1C9))),
    keyBackground = Color(0xFFFFC1E3),
    keyText = Color(0xFF4A1A3C),
    specialKeyBackground = Color(0xFFFFA1C9),
    specialKeyText = Color(0xFF4A1A3C),
    suggestionBackground = Color(0xFFFFD0E7),
    suggestionText = Color(0xFF4A1A3C),
    popupBackground = Color(0xFFFFA1C9),
    popupText = Color(0xFF4A1A3C)
)

fun citrusGradientKeyboardColors() = KeyboardColors(
    backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFFF7AE), Color(0xFFFFD3A0))),
    keyBackground = Color(0xFFFFF7AE),
    keyText = Color(0xFF5A3E00),
    specialKeyBackground = Color(0xFFFFD3A0),
    specialKeyText = Color(0xFF5A3E00),
    suggestionBackground = Color(0xFFFFEDB0),
    suggestionText = Color(0xFF5A3E00),
    popupBackground = Color(0xFFFFD3A0),
    popupText = Color(0xFF5A3E00)
)

fun skyGradientKeyboardColors() = KeyboardColors(
    backgroundBrush = Brush.linearGradient(listOf(Color(0xFFA6EFFF), Color(0xFF70D6FF))),
    keyBackground = Color(0xFFA6EFFF),
    keyText = Color(0xFF00334D),
    specialKeyBackground = Color(0xFF70D6FF),
    specialKeyText = Color(0xFF00334D),
    suggestionBackground = Color(0xFF8EE5FF),
    suggestionText = Color(0xFF00334D),
    popupBackground = Color(0xFF70D6FF),
    popupText = Color(0xFF00334D)
)

fun mintGradientKeyboardColors() = KeyboardColors(
    backgroundBrush = Brush.linearGradient(listOf(Color(0xFFA8E6CF), Color(0xFFB9FBC0))),
    keyBackground = Color(0xFFA8E6CF),
    keyText = Color(0xFF0F3D2F),
    specialKeyBackground = Color(0xFFB9FBC0),
    specialKeyText = Color(0xFF0F3D2F),
    suggestionBackground = Color(0xFFCFF2E3),
    suggestionText = Color(0xFF0F3D2F),
    popupBackground = Color(0xFFB9FBC0),
    popupText = Color(0xFF0F3D2F)
)

fun coralGradientKeyboardColors() = KeyboardColors(
    backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFFB5A7), Color(0xFFFF677D))),
    keyBackground = Color(0xFFFFB5A7),
    keyText = Color(0xFF4A1F1F),
    specialKeyBackground = Color(0xFFFF677D),
    specialKeyText = Color(0xFF4A1F1F),
    suggestionBackground = Color(0xFFFF9FA5),
    suggestionText = Color(0xFF4A1F1F),
    popupBackground = Color(0xFFFF677D),
    popupText = Color(0xFF4A1F1F)
)

fun electricNeonKeyboardColors() = KeyboardColors(
    backgroundBrush = Brush.linearGradient(listOf(Color(0xFFFF007F), Color(0xFF00FFFF))),
    // Hot Pink keys
    keyBackground = Color(0xFFFF007F),
    keyText = Color.White,
    // Electric Cyan special keys
    specialKeyBackground = Color(0xFF00FFFF),
    specialKeyText = Color.Black, // High contrast with cyan
    // Lighter Pink suggestions
    suggestionBackground = Color(0xFFFF4D94),
    suggestionText = Color.Black,
    popupBackground = Color(0xFF00FFFF),
    popupText = Color.Black
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
        KeyboardTheme.Pink -> pinkKeyboardColors()
        KeyboardTheme.Teal -> tealKeyboardColors()
        KeyboardTheme.Orange -> orangeKeyboardColors()
        KeyboardTheme.Green -> greenKeyboardColors()
        KeyboardTheme.Red -> redKeyboardColors()
        KeyboardTheme.Gray -> grayKeyboardColors()

        KeyboardTheme.SunsetGradient -> sunsetGradientKeyboardColors()
        KeyboardTheme.OceanGradient -> oceanGradientKeyboardColors()
        KeyboardTheme.ForestGradient -> forestGradientKeyboardColors()
        KeyboardTheme.LavenderGradient -> lavenderGradientKeyboardColors()
        KeyboardTheme.RoseGradient -> roseGradientKeyboardColors()
        KeyboardTheme.CitrusGradient -> citrusGradientKeyboardColors()
        KeyboardTheme.SkyGradient -> skyGradientKeyboardColors()
        KeyboardTheme.MintGradient -> mintGradientKeyboardColors()
        KeyboardTheme.CoralGradient -> coralGradientKeyboardColors()
        KeyboardTheme.ElectricNeonGradient -> electricNeonKeyboardColors()
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