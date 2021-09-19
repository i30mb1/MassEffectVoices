package n7.mev.ui.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.mev.ui.heroes.usecase.GetHeroesVOUseCase
import n7.mev.ui.heroes.vo.HeroVO

class HeroesViewModel(
    application: Application
) : AndroidViewModel(application) {

    sealed class State {
        object Loading : State()
        data class Data(val list: List<HeroVO>) : State()
    }


    private val featureManager = FeatureManager(viewModelScope, application)
    private val getHeroesVOUseCase = GetHeroesVOUseCase(application, Dispatchers.IO)

    val state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val featureStatus = featureManager.status

    fun load() {
        getHeroesVOUseCase(featureManager.getInstalledModules())
            .onEach { list -> state.emit(State.Data(list)) }
            .launchIn(viewModelScope)
    }


}