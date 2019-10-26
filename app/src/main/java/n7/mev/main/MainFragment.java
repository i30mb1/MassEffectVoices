package n7.mev.main;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import n7.mev.R;
import n7.mev.databinding.MainFragmentBinding;
import n7.mev.util.SnackbarUtils;

import static n7.mev.modules.ModulesFragment.MODULE_NAME;

public class MainFragment extends Fragment {

    public static int REQUESTED_PERMISSION = 1;
    private MainViewModel mViewModel;
    private String LAST_VISIBLE_ITEM = "LAST_VISIBLE_ITEM";
    private String LAST_SELECTED_HERO = "LAST_SELECTED_HERO";
    private MainFragmentBinding binding;
    private LinearLayoutManager linearLayoutManager;
    private SoundPagedListAdapter adapter;
    private int lastVisibleItem = 0;
    private String moduleName;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar() {
        ((MainActivity) getActivity()).setSupportActionBar(binding.toolbar);
        getActivity().setTitle("");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            lastVisibleItem = savedInstanceState.getInt(LAST_VISIBLE_ITEM);
            moduleName = savedInstanceState.getString(LAST_SELECTED_HERO);
        } else {
            moduleName = getActivity().getIntent().getStringExtra(MODULE_NAME);
        }

        mViewModel = ViewModelProviders.of(getActivity(), new MainViewModelFactory(getActivity().getApplication(), moduleName)).get(MainViewModel.class);

        mViewModel.createPagedListData(lastVisibleItem).observe(this, new Observer<PagedList<SoundModel>>() {
            @Override
            public void onChanged(PagedList<SoundModel> soundModels) {
                adapter.submitList(soundModels);
            }
        });

        binding.setViewModel(mViewModel);
        binding.executePendingBindings();

//        int resourceId = getResources().getIdentifier(moduleName, "drawable", getActivity().getPackageName());
//        binding.ivMainFragmentHero.setImageResource(resourceId);

        setupToolbar();
        setupAllSnackBars();
        setupRecyclerView();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        lastVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        outState.putInt(LAST_VISIBLE_ITEM, lastVisibleItem);
        outState.putString(LAST_SELECTED_HERO, moduleName);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setupAllSnackBars() {
        mViewModel.getShowSnackBarFolder().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                SnackbarUtils.showSnackbarWithAction(getView(),
                        getString(R.string.file_loaded),
                        getString(R.string.open),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri mydir = Uri.parse(Environment.getExternalStorageDirectory().getPath() + File.separator + getString(R.string.app_name));
                                intent.setDataAndType(mydir, "*/*");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getContext(),"saved in : " + mydir.toString(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        mViewModel.getGrandSettingEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                SnackbarUtils.showSnackbarWithAction(getView(),
                        getString(R.string.all_grand_permission),
                        getString(R.string.all_enable),
                        new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent, REQUESTED_PERMISSION);
                            }
                        });
            }
        });
        mViewModel.getGrandSPermission().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                if (getActivity() != null) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTED_PERMISSION);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mViewModel.handleActivityResult(requestCode, resultCode, data);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.rvMainFragment;
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SoundPagedListAdapter(mViewModel);
        recyclerView.setAdapter(adapter);
    }

}
