package n7.mev.ui.heroes.usecase

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.mev.ui.heroes.vo.HeroVO

class GetHeroesVOUseCase constructor(
    private val application: Application,
    private val dispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(): List<HeroVO> = withContext(dispatcher) {
        emptyList()
    }

}