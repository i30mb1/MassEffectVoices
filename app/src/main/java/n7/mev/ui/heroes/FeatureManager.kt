package n7.mev.ui.heroes

import android.app.Application
import androidx.fragment.app.Fragment
import com.google.android.play.core.ktx.requestProgressFlow
import com.google.android.play.core.ktx.startConfirmationDialogForResult
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import n7.mev.BuildConfig
import kotlin.math.roundToInt


class FeatureManager(
    private val scope: CoroutineScope,
    private val application: Application,
) {

    sealed class State {
        object Nothing : State()
        object Canceled : State()
        object Error : State()
        object Installed : State()
        data class Data(val availableModules: Set<String>, val readyToInstallModules: Set<String>) : State()
        data class RequiredInformation(val state: SplitInstallSessionState) : State()
        data class Downloading(val totalBytes: Int, val currentBytes: Int) : State()
    }

    //    FakeSplitInstallManagerFactory
    private val installManager = SplitInstallManagerFactory.create(application)
    val status: MutableStateFlow<State> = MutableStateFlow(State.Data(getInstalledModules(), getModulesThatCanBeInstalled()))

    private val installManagerStatus = installManager.requestProgressFlow()
        .map { state: SplitInstallSessionState ->
            when (state.status()) {
                REQUIRES_USER_CONFIRMATION -> State.RequiredInformation(state)
                DOWNLOADING -> {
                    val totalBytes = state.totalBytesToDownload().toFloat().roundToInt()
                    val currentBytes = state.bytesDownloaded().toFloat().roundToInt()
                    State.Downloading(totalBytes, currentBytes)
                }
                CANCELED -> State.Canceled
                INSTALLED -> State.Installed
                FAILED -> State.Error
                else -> Unit
            }
        }
        .filterIsInstance<State>()
        .onEach { featureState -> status.emit(featureState) }
        .onEach { status.emit(State.Data(getInstalledModules(), getModulesThatCanBeInstalled())) }
        .launchIn(scope)

    fun startConfirmationDialog(fragment: Fragment, state: SplitInstallSessionState) {
        installManager.startConfirmationDialogForResult(state, fragment, 0)
    }

    fun installModule(moduleName: String) {
        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()
        installManager.startInstall(request)
    }

    fun getModulesThatCanBeInstalled(): Set<String> {
        val availableModules = getAllModules()
        return availableModules
        val installedModules = getInstalledModules()
        return availableModules - installedModules
    }

    private fun getInstalledModules(): Set<String> {
        return installManager.installedModules
    }

    private fun getAllModules(): Set<String> {
        return BuildConfig.modules
            .map { name -> name.drop(1) }
            .toSet()
    }

}