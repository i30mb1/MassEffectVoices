package n7.mev.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

internal val MevShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(28.dp),
)

/** The app is dark only: the N7 look has no light variant. */
@Composable
fun MevTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MevColorScheme,
        typography = MevTypography,
        shapes = MevShapes,
    ) {
        // screens are drawn on the dark background outside of a Surface too
        CompositionLocalProvider(LocalContentColor provides MevColorScheme.onBackground, content = content)
    }
}
