package n7.mev.ui.sounds.usecase

import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.mev.data.source.local.MePart
import n7.mev.data.source.local.SoundRepository
import n7.mev.ui.sounds.vo.SoundSectionVO
import n7.mev.ui.sounds.vo.SoundVO
import java.util.concurrent.ConcurrentHashMap

class GetSoundsVOUseCase(
    private val soundRepository: SoundRepository,
    private val dispatcher: CoroutineDispatcher,
) {

    private val lineIdRegex = Regex("\\d{6,}")
    private val cache = ConcurrentHashMap<String, List<SoundSectionVO>>()

    /** Emits the parts one by one, so the first one shows before the others are read. Cached per process. */
    operator fun invoke(moduleName: String): Flow<Pair<MePart, List<SoundSectionVO>>> = flow {
        for (part in soundRepository.getMEParts(moduleName)) {
            val sections = cache.getOrPut("$moduleName/$part") { load(moduleName, part) }
            emit(part to sections)
        }
    }.flowOn(dispatcher)

    private fun load(moduleName: String, part: MePart): List<SoundSectionVO> {
        return soundRepository.getSounds(moduleName, part).map { (folder, sounds) ->
            SoundSectionVO(
                folder = folder,
                sounds = sounds.map { sound ->
                    SoundVO(
                        lineId = lineIdRegex.findAll(sound.name).lastOrNull()?.value ?: sound.name.substringBeforeLast('.'),
                        fileName = sound.name,
                        folder = folder,
                        part = part,
                        assetPath = sound.path,
                        soundUri = sound.url.toUri(),
                    )
                },
            )
        }
    }
}
