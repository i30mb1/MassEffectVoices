package n7.mev.modules

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import n7.mev.R
import n7.mev.data.source.local.FeatureModule

fun <T> MutableLiveData<T>.setSingleEvent(value: T) {
    this.value = value
    this.value = null
}

class ModulesViewModel(application: Application) : AndroidViewModel(application), SplitInstallStateUpdatedListener {

    companion object {
        const val PREFIX = "feature_"
    }

    private val _installedModules = MutableLiveData<List<FeatureModule>>()
    val installedModules: LiveData<List<FeatureModule>> = _installedModules

    private val _buttonVisibility = MutableLiveData<Boolean>()
    val buttonVisibility: LiveData<Boolean> = _buttonVisibility

    private val _showMessage = MutableLiveData<Int>()
    val showMessage: LiveData<Int?> = _showMessage

    private val _showConfirmationDialog = MutableLiveData<Boolean>()
    val showConfirmationDialog: LiveData<Boolean?> = _showConfirmationDialog

    private val splitInstallManager: SplitInstallManager = SplitInstallManagerFactory.create(application)


    @kotlin.jvm.JvmField
    var isLoading = ObservableBoolean(false)

    @kotlin.jvm.JvmField
    var pbMaxValue = ObservableInt()

    @kotlin.jvm.JvmField
    var pbProgressValue = ObservableInt()
    private var splitInstallSessionState = MutableLiveData<SplitInstallSessionState>()
    private var context: Context
    private val allModules = hashSetOf(
            "legion", "edi", "garrus", "grunt", "illusive_man", "jack", "feature_avina",
            "jacob", "joker", "kasumi", "liara", "miranda", "mordin", "zaeed",
            "vega", "thane", "tali", "samara", "reaper", "javik", "dlc_citadel")


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
        _buttonVisibility.value = allModules.size != _installedModules.value!!.size
    }

    fun startConfirmationDialog(activity: Activity) {
        try {
            splitInstallManager.startConfirmationDialogForResult(splitInstallSessionState.value, activity, 0)
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
    }

    fun deleteModule(moduleName: String) {
        splitInstallManager.deferredUninstall(listOf(moduleName))
                .addOnSuccessListener { _showMessage.value = R.string.delete_when_ready }
        updateInstalledModules()
    }

    fun installedModules(): List<FeatureModule> {
        val availableList = allModules - splitInstallManager.installedModules.toList()
        return availableList.map { FeatureModule(it.replace(PREFIX, ""), it) }
    }

    fun installModule(moduleName: String) {
        if (!allModules.contains(moduleName)) return
        isLoading.set(true)
        _buttonVisibility.setValue(false)
        val request = SplitInstallRequest.newBuilder()
                .addModule(moduleName)
                .build()
        splitInstallManager.startInstall(request)
                .addOnFailureListener { e ->
                    when ((e as SplitInstallException).errorCode) {
                        SplitInstallErrorCode.NETWORK_ERROR -> {
                            _showMessage.value = R.string.network_error
                            isLoading.set(false)
                            _buttonVisibility.setValue(true)
                        }
                        else -> {
                            _showMessage.value = R.string.network_error
                            isLoading.set(false)
                            _buttonVisibility.setValue(true)
                        }
                    }
                }
                .addOnSuccessListener {
                    isLoading.set(false)
                    updateInstalledModules()
                }
    }

    fun refreshContext() {
        try {
            context = context.createPackageContext(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onStateUpdate(splitInstallSessionState: SplitInstallSessionState) {
        val status = splitInstallSessionState.status()
        when (status) {
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                _showMessage.setSingleEvent(R.string.require)
                this.splitInstallSessionState.value = splitInstallSessionState
                _showConfirmationDialog.setSingleEvent(true)
            }
            SplitInstallSessionStatus.DOWNLOADING -> {
                displayLoadingState(splitInstallSessionState)
                //                showSnackbar.setValue(R.string.downloading);
                isLoading.set(true)
                _buttonVisibility.setValue(false)
            }
            SplitInstallSessionStatus.INSTALLING -> {
                displayLoadingState(splitInstallSessionState)
                _showMessage.setSingleEvent(R.string.installing)
                isLoading.set(true)
                _buttonVisibility.setValue(false)
            }
            SplitInstallSessionStatus.INSTALLED -> {
                _showMessage.setSingleEvent(R.string.installed)
                isLoading.set(false)
                _buttonVisibility.setValue(true)
                updateInstalledModules()
            }
            SplitInstallSessionStatus.FAILED -> {
                val error = splitInstallSessionState.errorCode().toString() + " for module " + splitInstallSessionState.moduleNames()
                Toast.makeText(getApplication(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayLoadingState(state: SplitInstallSessionState) {
        val max = Math.round(state.totalBytesToDownload().toFloat())
        pbMaxValue.set(max)
        val progress = Math.round(state.bytesDownloaded().toFloat())
        pbProgressValue.set(progress)
    }

    override fun onCleared() {
        splitInstallManager.unregisterListener(this)
        super.onCleared()
    }

}