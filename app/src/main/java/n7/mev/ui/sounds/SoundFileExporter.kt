package n7.mev.ui.sounds

import android.app.Application
import android.content.ContentValues
import android.media.RingtoneManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import n7.mev.ui.sounds.vo.SoundVO

/** Copies a sound out of the module assets through MediaStore: no storage permission needed on API 30+. */
class SoundFileExporter(
    private val application: Application,
) {

    fun download(sound: SoundVO): Uri {
        return insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, sound, "${Environment.DIRECTORY_DOWNLOADS}/MEV")
    }

    /** Requires [android.provider.Settings.System.canWrite]. */
    fun setAsRingtone(sound: SoundVO) {
        val uri = insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, sound, "${Environment.DIRECTORY_RINGTONES}/MEV") {
            put(MediaStore.Audio.Media.IS_RINGTONE, 1)
        }
        RingtoneManager.setActualDefaultRingtoneUri(application, RingtoneManager.TYPE_RINGTONE, uri)
    }

    private fun insert(
        collection: Uri,
        sound: SoundVO,
        relativePath: String,
        extra: ContentValues.() -> Unit = {},
    ): Uri {
        val resolver = application.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, sound.fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
            extra()
        }
        val uri = requireNotNull(resolver.insert(collection, values)) { "MediaStore refused ${sound.fileName}" }
        try {
            requireNotNull(resolver.openOutputStream(uri)).use { output ->
                application.assets.open(sound.assetPath).use { input -> input.copyTo(output) }
            }
            resolver.update(uri, ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }, null, null)
        } catch (error: Exception) {
            resolver.delete(uri, null, null)
            throw error
        }
        return uri
    }

}
