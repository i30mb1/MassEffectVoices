package n7.mev.ui.sounds.usecase

import android.app.Application
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import n7.mev.data.source.local.MePart
import n7.mev.data.source.local.SoundRepository
import n7.mev.ui.sounds.vo.SoundVO

class GetSoundsVOUseCase constructor(
    private val application: Application,
    private val soundRepository: SoundRepository,
    private val dispatcher: CoroutineDispatcher,
) {

    operator fun invoke(moduleName: String, mePart: MePart): Flow<List<SoundVO>> = flow {
        val result = soundRepository.getSoundFolders(moduleName, mePart)
            .flatMapMerge { folder -> soundRepository.getSounds("$moduleName/$mePart/$folder") }
            .map { sound -> SoundVO(sound.name, sound.folder, sound.url.toUri()) }
            .toList()
        emit(result)
    }.flowOn(dispatcher)
}