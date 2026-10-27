package n7.mev.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import n7.mev.ui.theme.MevColors

/**
 * The N7 motif: a wide dark band and a thin red band, both tilted by 20°.
 * [offset] is the left edge of the wide band, [width] its width; the red band follows it.
 */
fun Modifier.n7Stripes(
    offset: Dp,
    width: Dp,
    accentWidth: Dp = width * 0.3f,
    accent: Color = MevColors.Accent,
): Modifier = clipToBounds().drawBehind {
    val bandHeight = size.height * 1.6f
    val top = -size.height * 0.3f
    rotate(degrees = 20f, pivot = Offset(offset.toPx() + width.toPx(), size.height / 2)) {
        drawRect(MevColors.SurfaceContainer, Offset(offset.toPx(), top), Size(width.toPx(), bandHeight))
        drawRect(accent, Offset(offset.toPx() + width.toPx() * 1.25f, top), Size(accentWidth.toPx(), bandHeight))
    }
}

/** [progress] is read while drawing only: a ticking value redraws the track without recomposing anything. */
@Composable
fun ProgressTrack(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    height: Dp = 4.dp,
    track: Color = MevColors.Outline,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(track)
            .drawBehind {
                drawRect(MevColors.Accent, size = size.copy(width = size.width * progress().coerceIn(0f, 1f)))
            }
    )
}

enum class RoundButtonStyle { Filled, Tonal, Outlined, Plain }

@Composable
fun RoundIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: RoundButtonStyle = RoundButtonStyle.Tonal,
    size: Dp = 44.dp,
    iconSize: Dp = 18.dp,
) {
    val (container, content) = when (style) {
        RoundButtonStyle.Filled -> MevColors.Primary to Color.White
        RoundButtonStyle.Tonal -> MevColors.Surface to MevColors.OnSurface
        RoundButtonStyle.Outlined -> Color.Transparent to MevColors.OnSurface
        RoundButtonStyle.Plain -> Color.Transparent to MevColors.OnSurfaceVariant
    }
    val border = when (style) {
        RoundButtonStyle.Filled, RoundButtonStyle.Plain -> Modifier
        RoundButtonStyle.Tonal -> Modifier.border(1.dp, MevColors.Outline, CircleShape)
        RoundButtonStyle.Outlined -> Modifier.border(1.dp, MevColors.OutlineVariant, CircleShape)
    }
    IconButton(
        onClick = onClick,
        modifier = modifier.size(size).then(border),
        colors = IconButtonDefaults.iconButtonColors(containerColor = container, contentColor = content),
    ) {
        Icon(painterResource(icon), contentDescription = contentDescription, modifier = Modifier.size(iconSize))
    }
}
