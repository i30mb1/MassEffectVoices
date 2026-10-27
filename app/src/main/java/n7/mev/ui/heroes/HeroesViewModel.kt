package n7.mev.ui.heroes

import android.app.Application
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import n7.mev.appGraph
import n7.mev.ui.heroes.vo.HeroUI

class HeroesViewModel(
    application: Application
) : AndroidViewModel(application) {

    @Immutable
    data class UiState(
        val installed: List<HeroUI> = emptyList(),
        val available: List<HeroUI> = emptyList(),
        val download: Download? = null,
    )

    data class Download(val sessionId: Int, val moduleName: String, val heroName: String, val progress: Float)

    private val featureManager = FeatureManager(viewModelScope, application)
    private val getHeroesVOUseCase = application.appGraph.getHeroesVOUseCase

    private var lastModules: FeatureManager.State.Data? = null

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _confirmations = Channel<SplitInstallSessionState>(Channel.BUFFERED)
    val confirmations: Flow<SplitInstallSessionState> = _confirmations.receiveAsFlow()

    init {
        featureManager.status
            .onEach { status ->
                when (status) {
                    is FeatureManager.State.Data -> {
                        if (status == lastModules) return@onEach
                        lastModules = status
                        val installed = getHeroesVOUseCase(status.installedModules)
                        val available = getHeroesVOUseCase(status.readyToInstallModules)
                        _state.update { it.copy(installed = installed, available = available) }
                    }

                    is FeatureManager.State.Downloading -> {
                        val moduleName = status.moduleNames.firstOrNull() ?: return@onEach
                        val heroName = _state.value.available.firstOrNull { it.moduleName == moduleName }?.name ?: moduleName
                        val progress = if (status.totalBytes > 0) status.currentBytes.toFloat() / status.totalBytes else 0f
                        _state.update { it.copy(download = Download(status.sessionId, moduleName, heroName, progress)) }
                    }

                    is FeatureManager.State.RequiredInformation -> _confirmations.send(status.state)
                    FeatureManager.State.Installed -> {
                        // makes assets of the freshly installed module readable without a restart
                        SplitCompat.install(getApplication())
                        _state.update { it.copy(download = null) }
                    }

                    FeatureManager.State.Canceled,
                    FeatureManager.State.Error -> _state.update { it.copy(download = null) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun installModule(moduleName: String) {
        featureManager.installModule(moduleName)
    }

    fun cancelDownload() {
        val download = _state.value.download ?: return
        featureManager.cancelInstall(download.sessionId)
    }

    fun startConfirmationDialog(state: SplitInstallSessionState, launcher: ActivityResultLauncher<IntentSenderRequest>) {
        featureManager.startConfirmationDialog(state, launcher)
    }

}
