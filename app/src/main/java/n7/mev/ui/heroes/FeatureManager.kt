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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

sealed class FeatureState {
    object Nothing : FeatureState()
    object Canceled : FeatureState()
    object Error : FeatureState()
    object Installed : FeatureState()
    data class RequiredInformation(val state: SplitInstallSessionState) : FeatureState()
    data class Downloading(val totalBytes: Int, val currentBytes: Int) : FeatureState()
}

class FeatureManager(
    private val scope: CoroutineScope,
    private val application: Application,
) {

    //    FakeSplitInstallManagerFactory
    private val installManager = SplitInstallManagerFactory.create(application)

    val status: MutableStateFlow<FeatureState> = MutableStateFlow(FeatureState.Nothing)
    private val installManagerStatus = installManager.requestProgressFlow()
        .map { state: SplitInstallSessionState ->
            when (state.status()) {
                REQUIRES_USER_CONFIRMATION -> FeatureState.RequiredInformation(state)
                DOWNLOADING -> {
                    val totalBytes = state.totalBytesToDownload().toFloat().roundToInt()
                    val currentBytes = state.bytesDownloaded().toFloat().roundToInt()
                    FeatureState.Downloading(totalBytes, currentBytes)
                }
                CANCELED -> FeatureState.Canceled
                INSTALLED -> FeatureState.Installed
                FAILED -> FeatureState.Error
                else -> Unit
            }
        }
        .filterIsInstance<FeatureState>()
        .onEach { status.emit(it) }
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

    fun getInstalledModules(): Set<String> {
        return installManager.installedModules
    }

    fun getAvailableModules() {

    }

}