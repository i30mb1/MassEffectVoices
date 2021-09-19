package n7.mev.ui.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.toList
import n7.mev.ui.heroes.usecase.GetHeroesVOUseCase
import n7.mev.ui.heroes.vo.HeroVO

class HeroesViewModel(
    application: Application
) : AndroidViewModel(application) {

    sealed class State {
        object Loading : State()
        data class Data(val list: List<HeroVO>, val isVisibleDownloadFeatureButton: Boolean) : State()
    }

    private val featureManager = FeatureManager(viewModelScope, application)
    private val getHeroesVOUseCase = GetHeroesVOUseCase(application, Dispatchers.IO)

    val state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val featureStatus = featureManager.status

    fun getReadyToInstallModules(): LiveData<List<HeroVO>> = flow {
        val list = featureManager.getReadyToInstallModules()
        val result = getHeroesVOUseCase(list).flatMapConcat { it.asFlow() }.toList()
        emit(result)
    }.asLiveData()

    fun load() {
        getHeroesVOUseCase(featureManager.getInstalledModules())
            .combine(featureManager.isModulesAvailable) { list: List<HeroVO>, isModulesAvailable: Boolean ->
                state.emit(State.Data(list, isModulesAvailable))
            }
            .launchIn(viewModelScope)
    }

}