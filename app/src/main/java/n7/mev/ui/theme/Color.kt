package n7.mev.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

internal object MevColors {
    val Background = Color(0xFF0B0C0E)
    val Scrim = Color(0xFF050607)
    val SurfaceDim = Color(0xFF121418)
    val Surface = Color(0xFF16181C)
    val SurfaceContainer = Color(0xFF1F2228)
    val CardBorder = Color(0xFF23262C)
    val Outline = Color(0xFF2A2E35)
    val OutlineVariant = Color(0xFF3A3F47)
    val OnSurface = Color(0xFFECEDEF)
    val OnSurfaceVariant = Color(0xFF9AA0A8)

    // N7 red: filled buttons (white text passes 4.5:1)
    val Primary = Color(0xFFD41A26)

    // N7 red: progress bars and the diagonal stripe
    val Accent = Color(0xFFE3202C)

    // N7 red that passes 4.5:1 as text on dark surfaces
    val AccentText = Color(0xFFFF5A63)
}

internal val MevColorScheme = darkColorScheme(
    primary = MevColors.Primary,
    onPrimary = Color.White,
    secondary = MevColors.Accent,
    onSecondary = Color.White,
    tertiary = MevColors.AccentText,
    background = MevColors.Background,
    onBackground = MevColors.OnSurface,
    surface = MevColors.Surface,
    onSurface = MevColors.OnSurface,
    surfaceVariant = MevColors.SurfaceContainer,
    onSurfaceVariant = MevColors.OnSurfaceVariant,
    surfaceDim = MevColors.SurfaceDim,
    surfaceContainer = MevColors.SurfaceContainer,
    surfaceContainerLow = MevColors.Surface,
    surfaceContainerHigh = MevColors.SurfaceContainer,
    outline = MevColors.Outline,
    outlineVariant = MevColors.OutlineVariant,
    scrim = MevColors.Scrim,
)
