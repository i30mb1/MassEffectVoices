package n7.mev.ui.heroes.usecase

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.mev.ui.heroes.vo.HeroVO

class GetHeroesVOUseCase constructor(
    private val application: Application,
    private val dispatcher: CoroutineDispatcher,
) {

    operator fun invoke(modulesSet: Set<String>): Flow<List<HeroVO>> = flow {
        val result = modulesSet.map { moduleName ->
            val icon = application.resources.getIdentifier(moduleName, "drawable", application.packageName)
            HeroVO(moduleName, icon, moduleName)
        }
        emit(result)
    }.flowOn(dispatcher)

}