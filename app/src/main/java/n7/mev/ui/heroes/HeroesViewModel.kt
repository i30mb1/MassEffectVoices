package n7.mev.ui.heroes

import android.app.Application
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import n7.mev.ui.heroes.usecase.GetHeroesVOUseCase

class HeroesViewModel(
    private val application: Application
) : ViewModel() {

    val getHeroesVOUseCase = GetHeroesVOUseCase(application, Dispatchers.IO)

    private val _error = Channel<Throwable>(Channel.BUFFERED)
    val error = _error.receiveAsFlow()

}