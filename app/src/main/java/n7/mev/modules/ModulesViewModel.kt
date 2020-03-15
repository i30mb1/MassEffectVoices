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
import androidx.lifecycle.MutableLiveData
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import n7.mev.R
import n7.mev.util.SnackbarMessage
import java.util.*

class ModulesViewModel(application: Application) : AndroidViewModel(application), SplitInstallStateUpdatedListener {
    val showSnackbar = SnackbarMessage()
    val startConfirmationDialog = SnackbarMessage()
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
    var splitInstallSessionState = MutableLiveData<SplitInstallSessionState>()
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
                .addOnSuccessListener { showSnackbar.value = R.string.deleted }
        showSnackbar.value = R.string.delete_when_ready
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
                            showSnackbar.value = R.string.network_error
                            isLoading.set(false)
                            showButtonAddModule.set(true)
                        }
                        else -> {
                            showSnackbar.value = R.string.network_error
                            isLoading.set(false)
                            showButtonAddModule.set(true)
                        }
                    }
                }
        //                .addOnCompleteListener(new OnCompleteListener<Integer>() {
//                    @Override
//                    public void onComplete(Task<Integer> task) {
//                        isLoading.set(false);
//                        updateModulesInSystem();
//                    }
//                });
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
                showSnackbar.value = R.string.require
                this.splitInstallSessionState.value = splitInstallSessionState
                startConfirmationDialog.call()
            }
            SplitInstallSessionStatus.DOWNLOADING -> {
                displayLoadingState(splitInstallSessionState)
                //                showSnackbar.setValue(R.string.downloading);
                isLoading.set(true)
                showButtonAddModule.set(false)
            }
            SplitInstallSessionStatus.INSTALLING -> {
                displayLoadingState(splitInstallSessionState)
                showSnackbar.value = R.string.installing
                isLoading.set(true)
                showButtonAddModule.set(false)
            }
            SplitInstallSessionStatus.INSTALLED -> {
                showSnackbar.value = R.string.installed
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