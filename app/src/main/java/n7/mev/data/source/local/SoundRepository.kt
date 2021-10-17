package n7.mev.data.source.local

import android.app.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

enum class MePart { ME1, ME2, ME3 }

class SoundRepository(
    private val application: Application,
) {

    data class Sound(val name: String, val folder: String, val url: String)

    fun getMEParts(module: String): Flow<MePart> = flow {
        application.assets.list(module)?.map { MePart.valueOf(it) }?.forEach { emit(it) }
    }

    fun getSoundFolders(module: String, mePart: MePart): Flow<String> = flow {
        application.assets.list("$module/$mePart")?.forEach { emit(it) }
    }

    fun getSounds(folder: String): Flow<Sound> = flow {
        application.assets.list(folder)?.forEach { name -> emit(Sound(name, folder, "asset:///$folder/$name")) }
    }

}