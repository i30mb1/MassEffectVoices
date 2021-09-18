package n7.mev.ui.heroes

import android.app.Application
import androidx.fragment.app.Fragment
import com.google.android.play.core.ktx.requestProgressFlow
import com.google.android.play.core.ktx.startConfirmationDialogForResult
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.CANCELED
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.DOWNLOADING
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.INSTALLED
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

sealed class FeatureState {
    object Canceled : FeatureState()
    object RequiredInformation : FeatureState()
    object Error : FeatureState()
    object Installed : FeatureState()
    data class Downloading(val totalBytes: Int, val currentBytes: Int) : FeatureState()
}

class FeatureManager(
    private val application: Application,
) : SplitInstallStateUpdatedListener {

    //    FakeSplitInstallManagerFactory
    private val installManager = SplitInstallManagerFactory.create(application)

    init {
        installManager.registerListener(this)
    }

    val status: Flow<FeatureState> = installManager.requestProgressFlow()
        .map { state: SplitInstallSessionState ->
            when (state.status()) {
                REQUIRES_USER_CONFIRMATION -> FeatureState.RequiredInformation
                DOWNLOADING -> {
                    val totalBytes = state.totalBytesToDownload().toFloat().roundToInt()
                    val currentBytes = state.bytesDownloaded().toFloat().roundToInt()
                    FeatureState.Downloading(totalBytes, currentBytes)
                }
                CANCELED -> FeatureState.Canceled
                INSTALLED -> FeatureState.Installed
                else -> FeatureState.Error
            }
        }

    override fun onStateUpdate(state: SplitInstallSessionState) {

    }

    fun onDestroy() {
        installManager.unregisterListener(this)
    }

    fun startConfirmationDialog(fragment: Fragment, state: SplitInstallSessionState) {
        installManager.startConfirmationDialogForResult(state, fragment, 0)
    }

    fun installModule(moduleName: String) {
        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()
        installManager.startInstall(request)
    }

}