package n7.mev.ui.heroes.usecase

import android.annotation.SuppressLint
import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.mev.data.source.local.SoundRepository
import n7.mev.ui.heroes.vo.HeroUI

class GetHeroesVOUseCase(
    private val application: Application,
    private val soundRepository: SoundRepository,
    private val dispatcher: CoroutineDispatcher,
) {

    // Portraits are named after their modules, so they are looked up by name
    @SuppressLint("DiscouragedApi")
    suspend operator fun invoke(modulesSet: Set<String>): List<HeroUI> = withContext(dispatcher) {
        modulesSet
            .map { moduleName ->
                val icon = application.resources.getIdentifier(moduleName, "drawable", application.packageName)
                HeroUI(displayName(moduleName), icon, moduleName, soundRepository.getMEParts(moduleName))
            }
            .sortedBy { it.name }
    }

    private fun displayName(moduleName: String): String = when (moduleName) {
        "edi" -> "EDI"
        else -> moduleName.split('_').joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
    }

}
