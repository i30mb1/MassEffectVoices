package n7.mev

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompatApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import n7.mev.data.source.local.SoundRepository
import n7.mev.ui.common.PortraitCache
import n7.mev.ui.heroes.usecase.GetHeroesVOUseCase
import n7.mev.ui.sounds.Player
import n7.mev.ui.sounds.SoundFileExporter
import n7.mev.ui.sounds.usecase.GetSoundsVOUseCase

class MevApplication : SplitCompatApplication() {

    val graph by lazy { AppGraph(this) }

}

/** Objects that are expensive to build or hold caches: created once per process. */
class AppGraph(application: Application) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val soundRepository = SoundRepository(application)
    val getSoundsVOUseCase = GetSoundsVOUseCase(soundRepository, Dispatchers.IO)
    val getHeroesVOUseCase = GetHeroesVOUseCase(application, soundRepository, Dispatchers.IO)
    val soundFileExporter = SoundFileExporter(application)
    val player = Player(application, appScope)
    val portraitCache = PortraitCache(application.resources, Dispatchers.IO)

}

val Context.appGraph: AppGraph get() = (applicationContext as MevApplication).graph
