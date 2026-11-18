package com.diarioturmas.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.view.WindowCompat
import com.diarioturmas.app.R

private val AppFont = FontFamily(
    Font(R.font.lato_regular, FontWeight.Normal),
    Font(R.font.lato_bold, FontWeight.Bold),
    Font(R.font.lato_black, FontWeight.ExtraBold),
)

private fun esquema(p: Palette, dark: Boolean) = if (dark) darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    secondary = Primary,
    background = p.bg,
    onBackground = p.textMain,
    surface = p.surface,
    onSurface = p.textMain,
    surfaceVariant = p.surfaceAlt,
    onSurfaceVariant = p.textMuted,
    outline = p.border,
    error = Neutral,
) else lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    secondary = Primary,
    background = p.bg,
    onBackground = p.textMain,
    surface = p.surface,
    onSurface = p.textMain,
    surfaceVariant = p.surfaceAlt,
    onSurfaceVariant = p.textMuted,
    outline = p.border,
    error = Danger,
)

@Composable
fun DiarioTurmasTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val paleta = if (dark) paletaEscura else paletaClara
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !dark
            controller.isAppearanceLightNavigationBars = !dark
        }
    }
    CompositionLocalProvider(LocalPalette provides paleta) {
        MaterialTheme(colorScheme = esquema(paleta, dark)) {
            ProvideTextStyle(LocalTextStyle.current.copy(fontFamily = AppFont)) {
                content()
            }
        }
    }
}
