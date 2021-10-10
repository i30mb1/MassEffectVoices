package n7.mev.data.source.local

import android.app.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SoundRepository(
    private val application: Application,
) {

    data class Sound(val name: String, val folder: String, val url: String)

    fun getSoundFolders(module: String): Flow<String> = flow {
        application.assets.list(module)?.forEach { emit(it) }
    }

    fun getSounds(folder: String): Flow<Sound> = flow {
        application.assets.list(folder)?.forEach { name -> emit(Sound(name, folder, "asset:///$folder/$name")) }
    }

}