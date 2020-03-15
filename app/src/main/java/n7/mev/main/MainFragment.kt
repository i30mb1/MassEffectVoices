package n7.mev.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.mev.R
import n7.mev.databinding.MainFragmentBinding
import n7.mev.modules.ModulesFragment
import n7.mev.util.SnackbarUtils
import java.io.File

class MainFragment : Fragment() {
    private var mViewModel: MainViewModel? = null
    private val LAST_VISIBLE_ITEM = "LAST_VISIBLE_ITEM"
    private val LAST_SELECTED_HERO = "LAST_SELECTED_HERO"
    private var binding: MainFragmentBinding? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: SoundPagedListAdapter? = null
    private var lastVisibleItem = 0
    private var moduleName: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        return binding.getRoot()
    }

    private fun setupToolbar() {
        (activity as MainActivity?)!!.setSupportActionBar(binding!!.toolbar)
        activity!!.title = ""
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            lastVisibleItem = savedInstanceState.getInt(LAST_VISIBLE_ITEM)
            moduleName = savedInstanceState.getString(LAST_SELECTED_HERO)
        } else {
            moduleName = activity!!.intent.getStringExtra(ModulesFragment.Companion.MODULE_NAME)
        }
        mViewModel = ViewModelProviders.of(activity!!, MainViewModelFactory(activity!!.application, moduleName)).get(MainViewModel::class.java)
        mViewModel!!.createPagedListData(lastVisibleItem).observe(this, Observer { soundModels -> adapter!!.submitList(soundModels) })
        binding!!.viewModel = mViewModel
        binding!!.executePendingBindings()

//        int resourceId = getResources().getIdentifier(moduleName, "drawable", getActivity().getPackageName());
//        binding.ivMainFragmentHero.setImageResource(resourceId);
        setupToolbar()
        setupAllSnackBars()
        setupRecyclerView()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lastVisibleItem = linearLayoutManager!!.findFirstCompletelyVisibleItemPosition()
        outState.putInt(LAST_VISIBLE_ITEM, lastVisibleItem)
        outState.putString(LAST_SELECTED_HERO, moduleName)
    }

    override fun onStop() {
        super.onStop()
    }

    private fun setupAllSnackBars() {
        mViewModel.getShowSnackBarFolder().observe(this, Observer {
            SnackbarUtils.showSnackbarWithAction(view,
                    getString(R.string.file_loaded),
                    getString(R.string.open)
            ) {
                val intent = Intent(Intent.ACTION_VIEW)
                val mydir = Uri.parse(Environment.getExternalStorageDirectory().path + File.separator + getString(R.string.app_name))
                intent.setDataAndType(mydir, "*/*")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                if (intent.resolveActivity(activity!!.packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "saved in : $mydir", Toast.LENGTH_LONG).show()
                }
            }
        })
        mViewModel.getGrandSettingEvent().observe(this, Observer {
            SnackbarUtils.showSnackbarWithAction(view,
                    getString(R.string.all_grand_permission),
                    getString(R.string.all_enable)
            ) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + context!!.packageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivityForResult(intent, REQUESTED_PERMISSION)
            }
        })
        mViewModel.getGrandSPermission().observe(this, Observer {
            if (activity != null) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUESTED_PERMISSION)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mViewModel!!.handleActivityResult(requestCode, resultCode, data)
    }

    private fun setupRecyclerView() {
        val recyclerView = binding!!.rvMainFragment
        linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        adapter = SoundPagedListAdapter(mViewModel)
        recyclerView.adapter = adapter
    }

    companion object {
        var REQUESTED_PERMISSION = 1
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}