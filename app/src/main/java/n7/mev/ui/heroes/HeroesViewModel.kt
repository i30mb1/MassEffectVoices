package n7.mev.ui.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import n7.mev.ui.heroes.usecase.GetHeroesVOUseCase
import n7.mev.ui.heroes.vo.HeroVO

class HeroesViewModel(
    application: Application
) : AndroidViewModel(application) {

    sealed class State {
        data class FeatureState(val featureState: FeatureManager.FeatureState) : State()
        data class Data(val list: List<HeroVO>, val isVisibleDownloadFeatureButton: Boolean) : State()
    }

    private val featureManager = FeatureManager(viewModelScope, application)
    private val getHeroesVOUseCase = GetHeroesVOUseCase(application, Dispatchers.IO)

    val status: Flow<State> = featureManager.status.map { status: FeatureManager.FeatureState ->
        when (status) {
            is FeatureManager.FeatureState.Data -> {
                val list = getHeroesVOUseCase(status.availableModules).single()
                State.Data(list, status.readyToInstallModules.isNotEmpty())
            }
            else -> State.FeatureState(status)
        }
    }

    suspend fun getReadyToInstallModules(): List<HeroVO> = flow {
        val list = featureManager.getReadyToInstallModules()
        val result = getHeroesVOUseCase(list).single()
        emit(result)
    }.single()

}