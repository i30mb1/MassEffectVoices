package n7.mev.ui.heroes

import android.app.Application
import androidx.fragment.app.Fragment
import com.google.android.play.core.ktx.requestProgressFlow
import com.google.android.play.core.ktx.startConfirmationDialogForResult
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.CANCELED
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.DOWNLOADING
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.FAILED
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.INSTALLED
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.mev.BuildConfig
import kotlin.math.roundToInt


class FeatureManager(
    scope: CoroutineScope,
    application: Application,
) {

    sealed class State {
        object Canceled : State()
        object Error : State()
        object Installed : State()
        data class Data(val availableModules: Set<String>, val readyToInstallModules: Set<String>) : State()
        data class RequiredInformation(val state: SplitInstallSessionState) : State()
        data class Downloading(val totalBytes: Int, val currentBytes: Int) : State()
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
                .onEach { featureState ->
                    status.emit(featureState)
                    status.emit(State.Data(getInstalledModules(), getModulesThatCanBeInstalled()))
                }
                .collect()
        }
    }

    fun startConfirmationDialog(fragment: Fragment, state: SplitInstallSessionState) {
        installManager.startConfirmationDialogForResult(state, fragment, 0)
    }

    fun installModule(moduleName: String) {
        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()
        installManager.startInstall(request).addOnFailureListener { error ->
            error
        }
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
        return BuildConfig.modules
            .map { name -> name.replace("", "") }
            .toSet()
    }

}
