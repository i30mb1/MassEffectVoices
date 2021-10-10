package n7.mev.data.source.local

import android.app.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SoundRepository(
    private val application: Application,
) {

    fun getSoundFolders(moduleName: String): Flow<String> = flow {
        application.assets.list(moduleName)?.forEach { emit(it) }
    }

    fun getSounds(pathToFolder: String): Flow<String> = flow {
        application.assets.list(pathToFolder)?.forEach { emit(it) }
    }

}