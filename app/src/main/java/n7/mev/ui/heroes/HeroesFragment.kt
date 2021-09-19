package n7.mev.ui.heroes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private lateinit var heroesAdapter: HeroesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HeroesFragmentBinding.bind(view)

        setupPagedListAdapter()
        setupListeners()
        viewModel.load()
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

    private fun setupListeners() {
        viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                when (it) {
                    is HeroesViewModel.State.Data -> heroesAdapter.submitList(it.list)
                    HeroesViewModel.State.Loading -> Unit
                }
            }
            .launchIn(lifecycleScope)
    }

    fun openModule(view: View?, moduleName: String?) {
        val intent = Intent(context, MainActivity::class.java)
//        intent.putExtra(MODULE_NAME, moduleName)
        startActivity(intent)
    }

    private fun setupPagedListAdapter() {
        val onHeroClickListener: (model: HeroVO) -> Unit = { }
        heroesAdapter = HeroesAdapter(layoutInflater, onHeroClickListener)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = heroesAdapter
        }
    }

}