package n7.mev.ui.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import n7.mev.ui.heroes.usecase.GetHeroesVOUseCase
import n7.mev.ui.heroes.vo.HeroVO

class HeroesViewModel(
    application: Application
) : AndroidViewModel(application) {

    sealed class State {
        data class FeatureManagerState(val featureState: FeatureManager.State) : State()
        data class Data(val list: List<HeroVO>, val isVisibleDownloadFeatureButton: Boolean) : State()
    }

    private val featureManager = FeatureManager(viewModelScope, application)
    private val getHeroesVOUseCase = GetHeroesVOUseCase(application, Dispatchers.IO)

    val status = featureManager.status.map { status: FeatureManager.State ->
        when (status) {
            is FeatureManager.State.Data -> {
                val list = getHeroesVOUseCase(status.availableModules).single()
                val isVisibleDownloadFeatureButton = status.readyToInstallModules.isNotEmpty()
                State.Data(list, isVisibleDownloadFeatureButton)
            }
            else -> State.FeatureManagerState(status)
        }
    }

    fun installModule(moduleName: String) {
        featureManager.installModule(moduleName)
    }

    fun startConfirmationDialog(fragment: HeroesFragment, state: SplitInstallSessionState) {
        featureManager.startConfirmationDialog(fragment, state)
    }

    suspend fun getReadyToInstallModules(): List<HeroVO> {
        val list = featureManager.getModulesThatCanBeInstalled()
        return getHeroesVOUseCase(list).single()
    }

}