package n7.mev.ui.sounds.vo

import android.net.Uri
import androidx.compose.runtime.Immutable
import n7.mev.data.source.local.MePart

@Immutable
data class SoundVO(
    /** Dialogue line id taken from the file name, e.g. `00129784`. */
    val lineId: String,
    val fileName: String,
    val folder: String,
    val part: MePart,
    val assetPath: String,
    val soundUri: Uri,
)

@Immutable
data class SoundSectionVO(
    val folder: String,
    val sounds: List<SoundVO>,
)
