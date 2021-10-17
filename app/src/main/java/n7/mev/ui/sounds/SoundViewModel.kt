package n7.mev.ui.sounds

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import n7.mev.data.source.local.MePart
import n7.mev.data.source.local.SoundRepository
import n7.mev.ui.sounds.usecase.GetSoundsVOUseCase
import n7.mev.ui.sounds.vo.SoundVO

class SoundViewModel(
    applicationContext: Application
) : AndroidViewModel(applicationContext) {

    private val _state: MutableLiveData<State> = MutableLiveData(State.Loading)
    val state: LiveData<State> = _state

    private val soundRepository = SoundRepository(applicationContext)
    private val getSoundsVOUseCase = GetSoundsVOUseCase(applicationContext, soundRepository, Dispatchers.IO)
    private val player = Player(applicationContext)

    fun play(url: Uri) {
        player.play(url)
    }

    fun load(moduleName: String) = viewModelScope.launch {
        _state.value = State.Data(getSoundsVOUseCase(moduleName, MePart.ME1).first())
    }

    companion object {
        sealed class State {
            object Loading : State()
            data class Data(val list: List<SoundVO>) : State()
        }
    }

}