package n7.mev.ui.sounds.usecase

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import n7.mev.data.source.local.SoundRepository
import n7.mev.ui.sounds.vo.SoundVO

class GetSoundsVOUseCase constructor(
    private val application: Application,
    private val soundRepository: SoundRepository,
    private val dispatcher: CoroutineDispatcher,
) {

    operator fun invoke(moduleName: String): Flow<List<SoundVO>> = flow {
        val result = soundRepository.getSoundFolders(moduleName)
            .flatMapMerge { folder -> soundRepository.getSounds("$moduleName/$folder") }
            .map { soundName -> SoundVO(soundName, soundName) }
            .toList()
        emit(result)
    }.flowOn(dispatcher)
}