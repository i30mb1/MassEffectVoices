package n7.mev.ui.heroes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import n7.mev.MainActivity
import n7.mev.R
import n7.mev.databinding.HeroesFragmentBinding
import n7.mev.ui.heroes.vo.HeroVO

class HeroesFragment private constructor() : Fragment(R.layout.heroes_fragment) {

    companion object {
        fun newInstance(): HeroesFragment {
            return HeroesFragment()
        }
    }

    private val viewModel: HeroesViewModel by viewModels()
    private lateinit var binding: HeroesFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HeroesFragmentBinding.bind(view)

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

    }

    private fun setupListeners() {
//        viewModel.error.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
//            .onEach { error -> Snackbar.make(binding.root, error.message.toString(), Snackbar.LENGTH_SHORT).show() }
//            .launchIn(lifecycleScope)
//
//        viewModel.showConfirmationDialog.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
//            .onEach { viewModel.startConfirmationDialog(requireActivity()) }
//            .launchIn(lifecycleScope)
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
//        intent.putExtra(MODULE_NAME, moduleName)
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
        val onHeroClickListener: (model: HeroVO) -> Unit = { }
        val modulesPagedListAdapter = HeroesAdapter(layoutInflater, onHeroClickListener)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = modulesPagedListAdapter
        }

    }

}