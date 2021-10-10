package n7.mev.ui.sounds.vo

import android.net.Uri

data class SoundVO(
    val name: String,
    val folder: String,
    val soundUri: Uri,
)