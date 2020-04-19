package n7.mev.modules

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import n7.mev.R
import n7.mev.databinding.BottomDrawerBinding
import n7.mev.databinding.DialogRateAppBinding
import n7.mev.databinding.ModulesFragmentBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ModulesFragment : Fragment(R.layout.modules_fragment) {

    private val viewModel: ModulesViewModel by viewModels()
    private lateinit var binding: ModulesFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ModulesFragmentBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
            it.fragment = this
        }

        setupPagedListAdapter()

        setupListeners()
        checkDayForDialogProm()
    }

    private fun openActivityFromModule() {
        if (viewModel.modulesCanBeInstall().contains("avina")) {
            return
        }

        val packageName = "n7.mev.avina"
        val avinaSampleClassName = "$packageName.SecretActivity"
        val intent = Intent()
        intent.setClassName(packageName, avinaSampleClassName)

        try {
            Class.forName(avinaSampleClassName, false, javaClass.classLoader)
        } catch (ignored: ClassNotFoundException) {

        }

        startActivity(intent)
    }

    fun showAvailableModules(view: View?) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding: BottomDrawerBinding = DataBindingUtil.inflate(layoutInflater, R.layout.bottom_drawer, null, false)
        bottomSheetDialog.setContentView(binding.root)
        for (module in viewModel.modulesCanBeInstall()) {
            binding.navBottomDrawer.menu.add(module)
        }
        binding.navBottomDrawer.setNavigationItemSelectedListener { item ->
            bottomSheetDialog.dismiss()
            viewModel.installModule(item.title.toString())
            true
        }
        bottomSheetDialog.show()
    }

    private fun setupListeners() {
//        viewModel.showSnackbar.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            it?.let {
//                SnackbarUtils.showSnackbar(view, getString(it))
//            }
//        })
//        viewModel.startConfirmationDialog.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            viewModel.startConfirmationDialog(requireActivity())
//        })
    }

    private fun checkDayForDialogProm() {
        val dialogShowed = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DIALOG_SHOWED, false)
        if (dialogShowed) return
        val currentDayInString = SimpleDateFormat("DDD", Locale.US).format(Calendar.getInstance().time)
        val currentDay = currentDayInString.toInt()
        val lastDay = PreferenceManager.getDefaultSharedPreferences(context).getInt(LAST_DAY, 0)
        if (currentDay == lastDay + 1) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(DIALOG_SHOWED, true).apply()
            showDialogRate()
        } else {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(LAST_DAY, currentDay).apply()
        }
    }

    private fun showDialogRate() {
        if (context == null) return
        val binding: DialogRateAppBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_rate_app, null, false)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window?.attributes?.windowAnimations = R.style.DialogTheme
        dialog.show()
        binding.bDialogRateApp.setOnClickListener {
            openAppStore()
            dialog.dismiss()
        }
    }

    private fun openAppStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + requireActivity().packageName)))
        } catch (a: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().packageName)))
        }
    }

    fun openModule(view: View?, moduleName: String?) {
//        val intent = Intent(context, MainActivity::class.java)
//        intent.putExtra(MODULE_NAME, moduleName)
//        if (activity != null) {
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), Pair(binding.ivModulesFragmentN7, "iv2"))
//            startActivity(intent, options.toBundle())
//        } else {
//            startActivity(intent)
//        }
    }

    fun showDialogDeleteModule(moduleName: String?): Boolean {
        if (context != null) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(getString(R.string.dialog_delete_module))
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                viewModel.deleteModule(moduleName)
                dialog.dismiss()
            }
            builder.setNegativeButton(android.R.string.no) { dialog, which -> dialog.dismiss() }
            val dialog = builder.create()
            dialog.window?.attributes?.windowAnimations = R.style.DialogTheme
            dialog.show()
        }
        return true
    }

    private fun setupPagedListAdapter() {
        val modulesPagedListAdapter = ModulesPagedListAdapter(this)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = modulesPagedListAdapter
        }

        viewModel.installedModules.observe(viewLifecycleOwner, modulesPagedListAdapter::submitList)
    }


    companion object {
        const val MODULE_NAME = "MODULE_NAME"
        private const val LAST_DAY = "LAST_DAY"
        private const val DIALOG_SHOWED = "DIALOG_SHOWED"
        fun newInstance(): ModulesFragment {
            return ModulesFragment()
        }

        /** Use external media if it is available, our app's file directory otherwise */
//        fun getOutputDirectory(context: Context): File {
//            val appContext = context.applicationContext
//            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
//                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
//            }
//            return if (mediaDir != null && mediaDir.exists())
//                mediaDir else appContext.filesDir
//        }
    }
}