package n7.mev.ui.heroes

import android.app.Application
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.ktx.requestProgressFlow
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.CANCELED
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.DOWNLOADED
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.DOWNLOADING
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.FAILED
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.INSTALLED
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.INSTALLING
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.mev.BuildConfig


class FeatureManager(
    scope: CoroutineScope,
    application: Application,
) {

    sealed class State {
        object Canceled : State()
        object Error : State()
        object Installed : State()
        data class Data(val installedModules: Set<String>, val readyToInstallModules: Set<String>) : State()
        data class RequiredInformation(val state: SplitInstallSessionState) : State()
        data class Downloading(
            val sessionId: Int,
            val moduleNames: List<String>,
            val totalBytes: Long,
            val currentBytes: Long,
        ) : State()
    }

    //    FakeSplitInstallManagerFactory
    private val installManager = SplitInstallManagerFactory.create(application)
    val status: MutableSharedFlow<State> = MutableSharedFlow(1)

    init {
        scope.launch {
            status.emit(State.Data(getInstalledModules(), getModulesThatCanBeInstalled()))
            installManager.requestProgressFlow()
                .map { state: SplitInstallSessionState ->
                    when (state.status()) {
                        REQUIRES_USER_CONFIRMATION -> State.RequiredInformation(state)
                        DOWNLOADING, DOWNLOADED, INSTALLING -> State.Downloading(
                            sessionId = state.sessionId(),
                            moduleNames = state.moduleNames(),
                            totalBytes = state.totalBytesToDownload(),
                            currentBytes = if (state.status() == DOWNLOADING) state.bytesDownloaded() else state.totalBytesToDownload(),
                        )
                        CANCELED -> State.Canceled
                        INSTALLED -> State.Installed
                        FAILED -> State.Error
                        else -> Unit
                    }
                }
                .filterIsInstance<State>()
                .onEach { featureState ->
                    status.emit(featureState)
                    // the module sets change only when a session ends, not on every download tick
                    if (featureState is State.Installed || featureState is State.Canceled || featureState is State.Error) {
                        status.emit(State.Data(getInstalledModules(), getModulesThatCanBeInstalled()))
                    }
                }
                .collect()
        }
    }

    fun startConfirmationDialog(state: SplitInstallSessionState, launcher: ActivityResultLauncher<IntentSenderRequest>) {
        installManager.startConfirmationDialogForResult(state, launcher)
    }

    fun installModule(moduleName: String) {
        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()
        installManager.startInstall(request)
    }

    fun cancelInstall(sessionId: Int) {
        installManager.cancelInstall(sessionId)
    }

    fun getModulesThatCanBeInstalled(): Set<String> {
        val availableModules = getAllModules()
        val installedModules = getInstalledModules()
        return availableModules - installedModules
    }

    private fun getInstalledModules(): Set<String> {
        return installManager.installedModules
    }

    private fun getAllModules(): Set<String> {
        return BuildConfig.modules.toSet()
    }

}
