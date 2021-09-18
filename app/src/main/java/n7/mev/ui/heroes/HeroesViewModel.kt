package n7.mev.ui.heroes

import android.app.Activity
import android.app.Application
import android.content.IntentSender.SendIntentException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.google.android.play.core.ktx.requestProgressFlow
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import n7.mev.ui.heroes.usecase.GetHeroesVOUseCase
import n7.mev.ui.heroes.vo.HeroVO
import kotlin.math.roundToInt

class HeroesViewModel(
    private val application: Application
) : ViewModel(), SplitInstallStateUpdatedListener {

    val getHeroesVOUseCase = GetHeroesVOUseCase(application, Dispatchers.IO)

    val list: LiveData<List<HeroVO>> = liveData {
        val result = getHeroesVOUseCase()
        emit(result)
    }

    var pbMaxValue = MutableLiveData(0)
    var pbProgressValue = MutableLiveData(0)

    private val _isLoading = MutableLiveData<Boolean>()
    var isLoading: LiveData<Boolean> = _isLoading

    private val _error = Channel<Throwable>(Channel.BUFFERED)
    val error = _error.receiveAsFlow()

    private val _showConfirmationDialog = Channel<Unit>(Channel.BUFFERED)
    val showConfirmationDialog = _showConfirmationDialog.receiveAsFlow()

    private val splitInstallManager: SplitInstallManager = SplitInstallManagerFactory.create(application)
    val statusManager: LiveData<String> = splitInstallManager.requestProgressFlow()
        .map { state ->
            when (state.status()) {
                SplitInstallSessionStatus.CANCELED -> {
                    ""
                }
                else -> {
                    ""
                }
            }
        }
        .catch { error -> _error.send(error) }
        .asLiveData()

    private var splitInstallSessionState = MutableLiveData<SplitInstallSessionState>()

    init {
        splitInstallManager.registerListener(this)
        updateInstalledModules()
    }

    private fun updateInstalledModules() {
        val moduleList = splitInstallManager.installedModules

    }

    fun startConfirmationDialog(activity: Activity) {
        try {
            splitInstallManager.startConfirmationDialogForResult(splitInstallSessionState.value!!, activity, 0)
        } catch (e: SendIntentException) {
            _error.offer(e)
        }
    }

//    fun deleteModule(moduleName: String) {
//        splitInstallManager.deferredUninstall(listOf(moduleName))
//                .addOnSuccessListener {
//
//                }
//        updateInstalledModules()
//    }

    fun installedModules() {
        val availableList = splitInstallManager.installedModules.toList()
    }

    fun installModule(moduleName: String) {
        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()
        splitInstallManager.startInstall(request)
            .addOnFailureListener { e ->
                _error.offer(e)
                _isLoading.value = false
            }
            .addOnSuccessListener {
                _isLoading.value = false
                updateInstalledModules()
            }
    }

    override fun onStateUpdate(splitInstallSessionState: SplitInstallSessionState) {
        when (splitInstallSessionState.status()) {
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                this.splitInstallSessionState.value = splitInstallSessionState
                _showConfirmationDialog.offer(Unit)
            }
            SplitInstallSessionStatus.DOWNLOADING -> {
                displayLoadingState(splitInstallSessionState)
                _isLoading.value = true
            }
            SplitInstallSessionStatus.INSTALLED -> {
                _isLoading.value = false
                updateInstalledModules()
            }
            SplitInstallSessionStatus.FAILED -> {
                _isLoading.value = false
                _error.offer(Throwable("${splitInstallSessionState.errorCode()} for module ${splitInstallSessionState.moduleNames()}"))
            }
            else -> {

            }
        }
    }

    private fun displayLoadingState(state: SplitInstallSessionState) {
        val max = state.totalBytesToDownload().toFloat().roundToInt()
        pbMaxValue.value = max
        val progress = state.bytesDownloaded().toFloat().roundToInt()
        pbProgressValue.value = progress
    }

    override fun onCleared() {
        splitInstallManager.unregisterListener(this)
        super.onCleared()
    }

}