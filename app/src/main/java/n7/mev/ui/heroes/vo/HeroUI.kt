package n7.mev.ui.heroes.vo

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import n7.mev.data.source.local.MePart

@Immutable
data class HeroUI(
    val name: String,
    @DrawableRes val icon: Int,
    val moduleName: String,
    /** Empty until the module is installed: its assets are not readable before that. */
    val parts: List<MePart> = emptyList(),
)
