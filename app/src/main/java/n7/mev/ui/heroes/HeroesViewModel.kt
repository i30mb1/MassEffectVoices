package n7.mev.ui.heroes

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.google.android.play.core.ktx.requestProgressFlow
import com.google.android.play.core.splitinstall.SplitInstallHelper
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import n7.mev.data.source.local.FeatureModule
import kotlin.math.roundToInt

fun <T> MutableLiveData<T>.setSingleEvent(value: T) {
    this.value = value
    this.value = null
}

class HeroesViewModel(application: Application) : AndroidViewModel(application), SplitInstallStateUpdatedListener {

    companion object {
        const val PREFIX = "feature_"
    }

    private val _installedModules = MutableLiveData<List<FeatureModule>>()
    val installedModules: LiveData<List<FeatureModule>> = _installedModules

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _simpleMessage = MutableLiveData<String?>()
    val simpleMessage: LiveData<String?> = _simpleMessage

    private val _showConfirmationDialog = MutableLiveData<Boolean>()
    val showConfirmationDialog: LiveData<Boolean?> = _showConfirmationDialog

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
        .catch { }
        .asLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    var isLoading: LiveData<Boolean> = _isLoading

    var pbMaxValue = MutableLiveData(0)
    var pbProgressValue = MutableLiveData(0)

    private var splitInstallSessionState = MutableLiveData<SplitInstallSessionState>()
    private var context: Context
    private val allModules = hashSetOf(
        "legion", "edi", "garrus", "grunt", "illusive_man", "jack", "feature_avina",
        "jacob", "joker", "kasumi", "liara", "miranda", "mordin", "zaeed",
        "vega", "thane", "tali", "samara", "reaper", "javik", "dlc_citadel"
    )


    init {
        context = application.applicationContext
        splitInstallManager.registerListener(this)
        updateInstalledModules()
    }

    private fun updateInstalledModules() {
        val moduleList: List<FeatureModule> = splitInstallManager.installedModules.map {
            FeatureModule(it.replace(PREFIX, ""), it)
        }
        _installedModules.value = moduleList
    }

    fun startConfirmationDialog(activity: Activity) {
        try {
            splitInstallManager.startConfirmationDialogForResult(splitInstallSessionState.value!!, activity, 0)
        } catch (e: SendIntentException) {
            _errorMessage.setSingleEvent(e.message)
        }
    }

//    fun deleteModule(moduleName: String) {
//        splitInstallManager.deferredUninstall(listOf(moduleName))
//                .addOnSuccessListener {
//
//                }
//        updateInstalledModules()
//    }

    fun installedModules(): List<FeatureModule> {
        val availableList = allModules - splitInstallManager.installedModules.toList()
        return availableList.map { FeatureModule(it.replace(PREFIX, ""), it) }
    }

    fun installModule(moduleName: String) {
        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()
        splitInstallManager.startInstall(request)
            .addOnFailureListener { e ->
                _errorMessage.setSingleEvent(e.message)
                _isLoading.value = false
            }
            .addOnSuccessListener {
                _isLoading.value = false
                updateInstalledModules()
            }
    }

    private fun refreshContext() {
        try {
            SplitInstallHelper.updateAppInfo(getApplication())
            context = context.createPackageContext(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onStateUpdate(splitInstallSessionState: SplitInstallSessionState) {
        when (splitInstallSessionState.status()) {
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                this.splitInstallSessionState.value = splitInstallSessionState
                _showConfirmationDialog.setSingleEvent(true)
            }
            SplitInstallSessionStatus.DOWNLOADING -> {
                displayLoadingState(splitInstallSessionState)
                _isLoading.value = true
            }
            SplitInstallSessionStatus.INSTALLED -> {
                _isLoading.value = false
                updateInstalledModules()
                refreshContext()
            }
            SplitInstallSessionStatus.FAILED -> {
                _isLoading.value = false
                _errorMessage.setSingleEvent("${splitInstallSessionState.errorCode()} for module ${splitInstallSessionState.moduleNames()}")
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