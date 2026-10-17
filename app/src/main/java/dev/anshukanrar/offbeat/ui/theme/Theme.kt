package dev.anshukanrar.offbeat.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Gold400,
    onPrimary = Night,
    primaryContainer = Gold900,
    onPrimaryContainer = Gold200,
    secondary = Ink400,
    onSecondary = Night,
    tertiary = Gold300,
    onTertiary = Night,

    background = Night,
    onBackground = Paper,
    surface = Ink900,
    onSurface = Paper,
    surfaceVariant = Ink800,
    onSurfaceVariant = Ink400,
    outline = Ink600,
    outlineVariant = DividerDark
)

private val LightColorScheme = lightColorScheme(
    primary = GoldBase,
    onPrimary = Night,
    primaryContainer = Gold100,
    onPrimaryContainer = Gold800,
    secondary = Ink700,
    onSecondary = Paper,
    tertiary = Gold700,
    onTertiary = Paper,

    background = Paper,
    onBackground = InkText,
    surface = PaperDim,
    onSurface = InkText,
    surfaceVariant = Ink200,
    onSurfaceVariant = Ink700,
    outline = Ink500,
    outlineVariant = DividerLight
)

@Composable
fun OffbeatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}