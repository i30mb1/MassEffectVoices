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
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import n7.mev.R
import java.util.*

fun <T> MutableLiveData<T>.setSingleEvent(value: T) {
    this.value = value
    this.value = null
}

class ModulesViewModel(application: Application) : AndroidViewModel(application), SplitInstallStateUpdatedListener {

    private val _showSnackbar = MutableLiveData<Int?>()
    val showSnackbar: LiveData<Int?> = _showSnackbar

    private val _startConfirmationDialog = MutableLiveData<Boolean?>()
    val startConfirmationDialog: LiveData<Boolean?> = _startConfirmationDialog
    val splitInstallManager: SplitInstallManager

    @kotlin.jvm.JvmField
    var showButtonAddModule = ObservableBoolean(true)
    var availableModules = MutableLiveData<Set<String>>()

    @kotlin.jvm.JvmField
    var isLoading = ObservableBoolean(false)

    @kotlin.jvm.JvmField
    var pbMaxValue = ObservableInt()

    @kotlin.jvm.JvmField
    var pbProgressValue = ObservableInt()
    private var splitInstallSessionState = MutableLiveData<SplitInstallSessionState>()
    private var context: Context
    private val allModules = HashSet(Arrays.asList(
            "legion", "edi", "garrus", "grunt", "illusive_man", "jack", "avina",
            "jacob", "joker", "kasumi", "liara", "miranda", "mordin", "zaeed",
            "vega", "thane", "tali", "samara", "reaper", "javik", "dlc_citadel"))

    fun startConfirmationDialog(activity: Activity?) {
        try {
            splitInstallManager.startConfirmationDialogForResult(splitInstallSessionState.value, activity, 0)
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
    }

    fun updateModulesInSystem() {
        val installedModules = splitInstallManager.installedModules
        availableModules.value = installedModules
        if (allModules.size == installedModules.size) {
            showButtonAddModule.set(false)
        } else {
            showButtonAddModule.set(true)
        }
    }

    fun deleteModule(moduleName: String?) {
        splitInstallManager.deferredUninstall(listOf(moduleName))
                .addOnSuccessListener { _showSnackbar.value = R.string.deleted }
        _showSnackbar.value = R.string.delete_when_ready
        updateModulesInSystem()
    }

    fun modulesCanBeInstall(): Set<String> {
        val installedModules = splitInstallManager.installedModules
        val list = allModules.clone() as MutableSet<String>
        list.removeAll(installedModules)
        return list
    }

    fun installModule(moduleName: String) {
        if (!allModules.contains(moduleName)) return
        isLoading.set(true)
        showButtonAddModule.set(false)
        val request = SplitInstallRequest.newBuilder()
                .addModule(moduleName)
                .build()
        splitInstallManager.startInstall(request)
                .addOnFailureListener { e ->
                    when ((e as SplitInstallException).errorCode) {
                        SplitInstallErrorCode.NETWORK_ERROR -> {
                            _showSnackbar.value = R.string.network_error
                            isLoading.set(false)
                            showButtonAddModule.set(true)
                        }
                        else -> {
                            _showSnackbar.value = R.string.network_error
                            isLoading.set(false)
                            showButtonAddModule.set(true)
                        }
                    }
                }
                .addOnSuccessListener {
                    isLoading.set(false)
                    updateModulesInSystem()
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
                _showSnackbar.setSingleEvent(R.string.require)
                this.splitInstallSessionState.value = splitInstallSessionState
                _startConfirmationDialog.setSingleEvent(true)
            }
            SplitInstallSessionStatus.DOWNLOADING -> {
                displayLoadingState(splitInstallSessionState)
                //                showSnackbar.setValue(R.string.downloading);
                isLoading.set(true)
                showButtonAddModule.set(false)
            }
            SplitInstallSessionStatus.INSTALLING -> {
                displayLoadingState(splitInstallSessionState)
                _showSnackbar.setSingleEvent(R.string.installing)
                isLoading.set(true)
                showButtonAddModule.set(false)
            }
            SplitInstallSessionStatus.INSTALLED -> {
                _showSnackbar.setSingleEvent(R.string.installed)
                isLoading.set(false)
                showButtonAddModule.set(true)
                updateModulesInSystem()
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

    init {
        context = application.applicationContext
        splitInstallManager = SplitInstallManagerFactory.create(context)
        splitInstallManager.registerListener(this)
        updateModulesInSystem()
    }
}