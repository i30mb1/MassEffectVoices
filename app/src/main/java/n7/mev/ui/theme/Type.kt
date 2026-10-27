package n7.mev.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import n7.mev.R

internal val Rajdhani = FontFamily(
    Font(R.font.rajdhani_semibold, FontWeight.SemiBold),
    Font(R.font.rajdhani_bold, FontWeight.Bold),
)

internal val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

internal val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
)

/** Rajdhani styles are meant to be shown in caps: pass `text.uppercase()`. */
internal val MevTypography = Typography(
    displaySmall = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 44.sp, lineHeight = 44.sp, letterSpacing = 2.sp),
    headlineSmall = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 32.sp, letterSpacing = 1.5.sp),
    titleLarge = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 28.sp, letterSpacing = 1.2.sp),
    titleMedium = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 19.sp, lineHeight = 22.sp, letterSpacing = 1.sp),
    titleSmall = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 20.sp, letterSpacing = 1.sp),
    labelLarge = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 20.sp, letterSpacing = 1.5.sp),
    bodyLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 20.sp),
    bodyMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = JetBrainsMono, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
)
