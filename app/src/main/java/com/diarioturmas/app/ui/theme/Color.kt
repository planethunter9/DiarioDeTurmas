package com.diarioturmas.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ===== DUAS CORES: um laranja de acento + cinzentos neutros =====

// Acento (único)
val Primary = Color(0xFFEE7A1A)
val PrimarySoft = Color(0xFFFCE7D4)

// Neutro
val Neutral = Color(0xFF8A8A90)
val NeutralSoft = Color(0xFFECECEF)

// Os restantes "acentos" passam a ser neutros — a app fica só com 2 cores.
val Success = Neutral
val SuccessSoft = NeutralSoft
val Danger = Neutral
val DangerSoft = NeutralSoft
val Warning = Neutral
val WarningSoft = NeutralSoft
val Violet = Neutral
val VioletSoft = NeutralSoft

// Texto legível sobre fundos "soft" (claros nos dois temas)
val OnSoftMain = Color(0xFF1C1C1E)
val OnSoftMuted = Color(0xFF6E6E73)

// ----- Paleta que muda com o tema (tons neutros) -----
data class Palette(
    val bg: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val textMain: Color,
    val textMuted: Color,
    val border: Color,
)

val paletaClara = Palette(
    bg = Color(0xFFF6F6F7),
    surface = Color(0xFFFFFFFF),
    surfaceAlt = Color(0xFFEFEFF1),
    textMain = Color(0xFF1C1C1E),
    textMuted = Color(0xFF6E6E73),
    border = Color(0xFFE6E6E9),
)

val paletaEscura = Palette(
    bg = Color(0xFF121214),
    surface = Color(0xFF1C1C1F),
    surfaceAlt = Color(0xFF26262A),
    textMain = Color(0xFFF2F2F4),
    textMuted = Color(0xFF9E9EA6),
    border = Color(0xFF303036),
)

val LocalPalette = staticCompositionLocalOf { paletaClara }

val Bg: Color @Composable @ReadOnlyComposable get() = LocalPalette.current.bg
val SurfaceC: Color @Composable @ReadOnlyComposable get() = LocalPalette.current.surface
val SurfaceAlt: Color @Composable @ReadOnlyComposable get() = LocalPalette.current.surfaceAlt
val TextMain: Color @Composable @ReadOnlyComposable get() = LocalPalette.current.textMain
val TextMuted: Color @Composable @ReadOnlyComposable get() = LocalPalette.current.textMuted
val BorderC: Color @Composable @ReadOnlyComposable get() = LocalPalette.current.border

// Categorias usam o acento (uma cor)
data class CoresCategoria(val cor: Color, val soft: Color)

fun coresCategoria(cat: String): CoresCategoria = CoresCategoria(Primary, PrimarySoft)
