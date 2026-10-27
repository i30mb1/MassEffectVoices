package n7.mev.ui.sounds

import android.app.Application
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import n7.mev.R
import n7.mev.appGraph
import n7.mev.data.source.local.MePart
import n7.mev.ui.heroes.vo.HeroUI
import n7.mev.ui.sounds.vo.SoundSectionVO
import n7.mev.ui.sounds.vo.SoundVO

/** One instance for every hero: [load] switches it to another module. */
class SoundViewModel(
    application: Application
) : AndroidViewModel(application) {

    @Immutable
    data class UiState(
        val isLoading: Boolean = true,
        val hero: HeroUI? = null,
        val parts: Map<MePart, List<SoundSectionVO>> = emptyMap(),
        val lineCounts: Map<MePart, Int> = emptyMap(),
        val selectedPart: MePart? = null,
        val playing: SoundVO? = null,
    ) {
        val sections: List<SoundSectionVO> get() = parts[selectedPart].orEmpty()
        val totalLines: Int get() = lineCounts.values.sum()
    }

    private val graph = application.appGraph
    private val player = graph.player

    private val content = MutableStateFlow(UiState())
    private var soundsByUri: Map<Uri, SoundVO> = emptyMap()
    private var loadJob: Job? = null

    val state: StateFlow<UiState> = combine(content, player.playing) { content, playingUri ->
        content.copy(playing = playingUri?.let(soundsByUri::get))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    /** Kept apart from [state]: it ticks 20 times a second while a line plays. */
    val progress: StateFlow<Float> = player.progress

    private val _messages = Channel<Int>(Channel.BUFFERED)
    val messages: Flow<Int> = _messages.receiveAsFlow()

    fun load(moduleName: String) {
        if (content.value.hero?.moduleName == moduleName) return
        loadJob?.cancel()
        soundsByUri = emptyMap()
        content.value = UiState()
        loadJob = viewModelScope.launch {
            val hero = graph.getHeroesVOUseCase(setOf(moduleName)).first()
            content.value = UiState(hero = hero)
            graph.getSoundsVOUseCase(moduleName).collect { (part, sections) ->
                val sounds = sections.flatMap { it.sounds }
                soundsByUri = soundsByUri + sounds.associateBy { it.soundUri }
                content.update { state ->
                    state.copy(
                        isLoading = false,
                        parts = state.parts + (part to sections),
                        lineCounts = state.lineCounts + (part to sounds.size),
                        selectedPart = state.selectedPart ?: part,
                    )
                }
            }
            content.update { it.copy(isLoading = false) }
        }
    }

    fun selectPart(part: MePart) {
        content.update { it.copy(selectedPart = part) }
    }

    fun toggle(sound: SoundVO) {
        if (player.playing.value == sound.soundUri) player.stop() else player.play(sound.soundUri)
    }

    fun play(sound: SoundVO) {
        player.play(sound.soundUri)
    }

    fun stop() {
        player.stop()
    }

    fun download(sound: SoundVO) = export(R.string.file_loaded) { graph.soundFileExporter.download(sound) }

    fun setAsRingtone(sound: SoundVO) = export(R.string.ringtone_set) { graph.soundFileExporter.setAsRingtone(sound) }

    private fun export(@StringRes success: Int, block: () -> Unit) {
        viewModelScope.launch {
            val result = runCatching { withContext(Dispatchers.IO) { block() } }
            _messages.send(if (result.isSuccess) success else R.string.action_failed)
        }
    }

}
