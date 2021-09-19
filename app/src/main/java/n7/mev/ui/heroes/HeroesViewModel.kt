package n7.mev.ui.heroes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import n7.mev.ui.heroes.usecase.GetHeroesVOUseCase

class HeroesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val featureManager = FeatureManager(viewModelScope, application)

    val getHeroesVOUseCase = GetHeroesVOUseCase(application, Dispatchers.IO)

    val state = featureManager.status

    fun load() {
        featureManager.getAvailableModules()
    }


}