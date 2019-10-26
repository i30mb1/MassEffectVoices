package n7.mev.modules;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.google.android.play.core.splitinstall.SplitInstallException;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import n7.mev.R;
import n7.mev.util.SnackbarMessage;

public class ModulesViewModel extends AndroidViewModel implements SplitInstallStateUpdatedListener {

    public final SnackbarMessage showSnackbar = new SnackbarMessage();
    public final SnackbarMessage startConfirmationDialog = new SnackbarMessage();
    public final SplitInstallManager splitInstallManager;
    public ObservableBoolean showButtonAddModule = new ObservableBoolean(true);
    public MutableLiveData<Set<String>> availableModules = new MutableLiveData<>();
    public ObservableBoolean isLoading = new ObservableBoolean(false);
    public ObservableInt pbMaxValue = new ObservableInt();
    public ObservableInt pbProgressValue = new ObservableInt();
    public MutableLiveData<SplitInstallSessionState> splitInstallSessionState = new MutableLiveData<>();
    private Context context;
    private HashSet<String> allModules = new HashSet<>(Arrays.asList(
            "legion", "edi", "garrus", "grunt", "illusive_man", "jack", "avina",
            "jacob", "joker", "kasumi", "liara", "miranda", "mordin", "zaeed",
            "vega", "thane", "tali", "samara", "reaper", "javik","dlc_citadel"));

    public ModulesViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        splitInstallManager = SplitInstallManagerFactory.create(context);
        splitInstallManager.registerListener(this);
        updateModulesInSystem();
    }

    public void startConfirmationDialog(Activity activity) {
        try {
            splitInstallManager.startConfirmationDialogForResult(splitInstallSessionState.getValue(), activity, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public void updateModulesInSystem() {
        Set<String> installedModules = splitInstallManager.getInstalledModules();
        availableModules.setValue(installedModules);

        if (allModules.size() == installedModules.size()) {
            showButtonAddModule.set(false);
        } else {
            showButtonAddModule.set(true);
        }
    }

    public void deleteModule(String moduleName) {
        splitInstallManager.deferredUninstall(Collections.singletonList(moduleName))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showSnackbar.setValue(R.string.deleted);
                    }
                });
        showSnackbar.setValue(R.string.delete_when_ready);
        updateModulesInSystem();
    }

    @SuppressWarnings("unchecked")
    public Set<String> modulesCanBeInstall() {
        Set<String> installedModules = splitInstallManager.getInstalledModules();
        Set<String> list = (Set<String>) allModules.clone();
        list.removeAll(installedModules);
        return list;
    }

    public void installModule(String moduleName) {
        if (!allModules.contains(moduleName)) return;

        isLoading.set(true);
        showButtonAddModule.set(false);

        SplitInstallRequest request = SplitInstallRequest.newBuilder()
                .addModule(moduleName)
                .build();
        splitInstallManager.startInstall(request)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        switch (((SplitInstallException) e).getErrorCode()) {
                            default:
                            case SplitInstallErrorCode.NETWORK_ERROR:
                                showSnackbar.setValue(R.string.network_error);
                                isLoading.set(false);
                                showButtonAddModule.set(true);
                                break;
                        }
                    }
                });
//                .addOnCompleteListener(new OnCompleteListener<Integer>() {
//                    @Override
//                    public void onComplete(Task<Integer> task) {
//                        isLoading.set(false);
//                        updateModulesInSystem();
//                    }
//                });
    }

    public void refreshContext() {
        try {
            context = context.createPackageContext(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStateUpdate(SplitInstallSessionState splitInstallSessionState) {
        int status = splitInstallSessionState.status();
        switch (status) {
            case SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION:
                showSnackbar.setValue(R.string.require);
                this.splitInstallSessionState.setValue(splitInstallSessionState);
                startConfirmationDialog.call();
//                splitInstallSessionState.resolutionIntent();
                break;
            case SplitInstallSessionStatus.DOWNLOADING:
                displayLoadingState(splitInstallSessionState);
//                showSnackbar.setValue(R.string.downloading);
                isLoading.set(true);
                showButtonAddModule.set(false);
                break;
            case SplitInstallSessionStatus.INSTALLING:
                displayLoadingState(splitInstallSessionState);
                showSnackbar.setValue(R.string.installing);
                isLoading.set(true);
                showButtonAddModule.set(false);
                break;
            case SplitInstallSessionStatus.INSTALLED:
                showSnackbar.setValue(R.string.installed);
                isLoading.set(false);
                showButtonAddModule.set(true);
                updateModulesInSystem();
                break;
            case SplitInstallSessionStatus.FAILED:
                String error = splitInstallSessionState.errorCode() + " for module " + splitInstallSessionState.moduleNames();
                Toast.makeText(getApplication(), error, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void displayLoadingState(SplitInstallSessionState state) {

        int max = Math.round(state.totalBytesToDownload());
        pbMaxValue.set(max);
        int progress = Math.round(state.bytesDownloaded());
        pbProgressValue.set(progress);

    }

    @Override
    protected void onCleared() {
        splitInstallManager.unregisterListener(this);
        super.onCleared();
    }
}

