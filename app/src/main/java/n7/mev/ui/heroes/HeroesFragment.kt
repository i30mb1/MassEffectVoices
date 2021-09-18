package n7.mev.ui.heroes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import n7.mev.MainActivity
import n7.mev.R
import n7.mev.databinding.ModulesFragmentBinding
import n7.mev.modules.ModulesPagedListAdapter
import n7.mev.modules.ModulesViewModel

class HeroesFragment : Fragment(R.layout.modules_fragment) {

    private val viewModel: ModulesViewModel by navGraphViewModels(R.id.nav_graph)
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
    }

    private fun openActivityFromModule() {
//        if (viewModel.installedModules().contains("avina")) {
//            return
//        }
//
//        val packageName = "n7.mev.avina"
//        val avinaSampleClassName = "$packageName.SecretActivity"
//        val intent = Intent()
//        intent.setClassName(packageName, avinaSampleClassName)
//
//        try {
//            Class.forName(avinaSampleClassName, false, javaClass.classLoader)
//        } catch (ignored: ClassNotFoundException) {
//
//        }
//
//        startActivity(intent)
    }

    fun showAvailableModules(view: View?) {
        findNavController().navigate(R.id.action_modulesFragment_to_bottomModulesSheetDialog)
    }

    private fun setupListeners() {
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            it?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show() }
        }
        viewModel.simpleMessage.observe(viewLifecycleOwner) {

        }
        viewModel.showConfirmationDialog.observe(viewLifecycleOwner) {
            it?.let { viewModel.startConfirmationDialog(requireActivity()) }
        }
    }

//    private fun openAppStore() {
//        try {
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + requireActivity().packageName)))
//        } catch (a: ActivityNotFoundException) {
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().packageName)))
//        }
//    }

    fun openModule(view: View?, moduleName: String?) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(MODULE_NAME, moduleName)
        startActivity(intent)
    }

    fun showDialogDeleteModule(moduleName: String): Boolean {
//        if (context != null) {
//            val builder = AlertDialog.Builder(requireContext())
//            builder.setMessage(getString(R.string.dialog_delete_module))
//            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
//                viewModel.deleteModule(moduleName)
//                dialog.dismiss()
//            }
//            builder.setNegativeButton(android.R.string.no) { dialog, which -> dialog.dismiss() }
//            val dialog = builder.create()
//            dialog.window?.attributes?.windowAnimations = R.style.DialogTheme
//            dialog.show()
//        }
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