package n7.mev.modules;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import n7.mev.R;
import n7.mev.databinding.BottomDrawerBinding;
import n7.mev.databinding.DialogRateAppBinding;
import n7.mev.databinding.ModulesFragmentBinding;
import n7.mev.main.MainActivity;
import n7.mev.purchaseUtils.IabHelper;
import n7.mev.purchaseUtils.IabResult;
import n7.mev.purchaseUtils.Inventory;
import n7.mev.purchaseUtils.Purchase;
import n7.mev.util.SnackbarUtils;

public class ModulesFragment extends Fragment {

    public static final String MODULE_NAME = "MODULE_NAME";
    private static final String LAST_DAY = "LAST_DAY";
    private static final String DIALOG_SHOWED = "DIALOG_SHOWED";
    private final String sku_beer = "once_per_month_subscription";
    private ModulesViewModel viewModel;
    private ModulesFragmentBinding binding;
    private ObjectAnimator scaleDown;

    static ModulesFragment newInstance() {
        return new ModulesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.modules_fragment, container, false);
        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(ModulesViewModel.class);
        binding.setFragment(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        setupListeners();
        setupToolbar();
        setupPagedListAdapter();
        checkDayForDialogProm();
        setupAnimation();
    }

    public boolean test(View view) {
//        openActivityFromModule();
//        startActionMode();
//        sendErrorResponse();
        return true;
    }

    private void sendErrorResponse() {
        Intent intent = new Intent(Intent.ACTION_APP_ERROR);
        startActivity(intent);
    }

    private void startActionMode() {
        ActionMode actionMode = ((ModulesActivity) getActivity()).startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_multi_download, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // при вызове invalidate
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // обработка нажатий на айтемы меню
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    private void openActivityFromModule() {
        if (viewModel.modulesCanBeInstall().contains("avina")) {
            return;
        }
        String packageName = "n7.mev.avina";
        String avinaSampleClassName = packageName + ".SecretActivity";

        Intent intent = new Intent();
        intent.setClassName(packageName, avinaSampleClassName);
        startActivity(intent);
    }

    @SuppressWarnings("ConstantConditions")
    public void showAvailableModules(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        BottomDrawerBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.bottom_drawer, null, false);
        bottomSheetDialog.setContentView(binding.getRoot());

        for (String module : viewModel.modulesCanBeInstall()) {
            binding.navBottomDrawer.getMenu().add(module);
        }
        binding.navBottomDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomSheetDialog.dismiss();
                viewModel.installModule(item.getTitle().toString());
                return true;
            }
        });

        bottomSheetDialog.show();
    }

    private void setupAnimation() {
        scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                binding.ivModulesFragmentN7,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0.8f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.8f)
        );
        scaleDown.setDuration(1000L);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
    }

    @Override
    public void onPause() {
        if (scaleDown != null) {
            scaleDown.cancel();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scaleDown != null) {
            scaleDown.start();
        }
    }

    private void setupListeners() {
        viewModel.showSnackbar.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer snackbarMessageResourceId) {
                SnackbarUtils.showSnackbar(getView(), getString(snackbarMessageResourceId));
            }
        });
        viewModel.startConfirmationDialog.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                viewModel.startConfirmationDialog(getActivity());
            }
        });
    }

    private void checkDayForDialogProm() {
        boolean dialogShowed = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(DIALOG_SHOWED, false);
        if (dialogShowed) return;

        String currentDayInString = new SimpleDateFormat("DDD", Locale.US).format(Calendar.getInstance().getTime());
        int currentDay = Integer.parseInt(currentDayInString);
        int lastDay = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(LAST_DAY, 0);
        if (currentDay == (lastDay + 1)) {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(DIALOG_SHOWED, true).apply();
            showDialogRate();
        } else {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt(LAST_DAY, currentDay).apply();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void showDialogRate() {
        if (getContext() == null) return;
        DialogRateAppBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_rate_app, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(binding.getRoot());

        final AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();

        binding.bDialogRateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppStore();
                dialog.dismiss();
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public void showDialogDonate() {
        if (getContext() == null) return;
        DialogRateAppBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_rate_app, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(binding.getRoot());

        binding.tvDialogRateAppTitle.setText(R.string.dialog_donate_title);
        binding.tvDialogRateAppSummary.setText(R.string.dialog_donate_summary);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();

        binding.bDialogRateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupPurchaseHelper();
                dialog.dismiss();
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void setupPurchaseHelper() {
        //todo key
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyr808/wY0nXtrh7WEp7ZXqkdZTcwgIaEfKvqKtjkR0KJm/O0B6mLAOn2lNYr8j/mrxprxUk1eLHJtUH2NGzCJzs40AdG5XL/2X34wQpKfLgHj95yybkOPQH7jtHdG15+jwkmT4X80icUpKfoEbHOLzwzQyUn97IdR4X2pQpbI4v5Xy6oYvBLtvcAZoYXUoTlUua+8N8ZGLFqTNlWeBk7rToC7jtXKEMDWYucjoMs2uyrZ2x04+/KFwakDH12MMsNmT1Xuo5Vx6gwZ9QflT6cMd5y0xQlXlGoWeeFUIEIMfyV4s1BSPs3LiYSgNrYLLJ24pznoPtW26Ro4xRpOV4cyQIDAQAB";
        final IabHelper mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (result.isSuccess()) {
                    buy(mHelper);
                }
            }
        });
    }

    private void buy(final IabHelper mHelper) {
        try {
            int requestCodePurchase = 1;
            mHelper.launchPurchaseFlow(getActivity(), sku_beer, requestCodePurchase, new IabHelper.OnIabPurchaseFinishedListener() {
                @Override
                public void onIabPurchaseFinished(IabResult result, Purchase info) {
                    if (mHelper == null) return;
                    if (result.isFailure()) return;
                    checkInventor(mHelper);
                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }

    }

    private void checkInventor(final IabHelper mHelper) {
        try {
            mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                    if (result.isFailure()) return;
                    if (inv.hasPurchase(sku_beer)) {
                        try {
                            mHelper.consumeAsync(inv.getPurchase(sku_beer), new IabHelper.OnConsumeFinishedListener() {
                                @Override
                                public void onConsumeFinished(Purchase purchase, IabResult result) {
                                    Snackbar.make(binding.getRoot(), "NICE", Snackbar.LENGTH_LONG).show();
                                    MediaPlayer.create(getContext(), R.raw.money).start();
                                }
                            });
                        } catch (IabHelper.IabAsyncInProgressException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void openAppStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getContext().getPackageName())));
        } catch (android.content.ActivityNotFoundException a) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar() {
        ((ModulesActivity) getActivity()).setSupportActionBar(binding.toolbar);
        getActivity().setTitle("");
    }

    public void openModule(View view, String moduleName) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(MODULE_NAME, moduleName);
        if (getActivity() != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    new Pair<View, String>(binding.ivModulesFragmentN7, "iv2")
//                    new Pair<View, String>(view.findViewById(R.id.iv_modules_item_icon), "iv")
            );
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public boolean showDialogDeleteModule(final String moduleName) {
        if (getContext() != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.dialog_delete_module));
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    viewModel.deleteModule(moduleName);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            dialog.show();
        }
        return true;
    }

    private void setupPagedListAdapter() {
        binding.rvModulesFragment.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        final ModulesPagedListAdapter adapter = new ModulesPagedListAdapter(this);
        binding.rvModulesFragment.setAdapter(adapter);
        viewModel.availableModules.observe(this, new Observer<Set<String>>() {
            @Override
            public void onChanged(Set<String> strings) {
                adapter.addAll(strings);
            }
        });
    }

}
