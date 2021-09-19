package n7.mev.ui.heroes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.mev.MainActivity
import n7.mev.R
import n7.mev.databinding.HeroesFragmentBinding
import n7.mev.ui.heroes.adapter.HeroesAdapter
import n7.mev.ui.heroes.adapter.OffsetItemDecorator
import n7.mev.ui.heroes.vo.HeroVO

class HeroesFragment private constructor() : Fragment(R.layout.heroes_fragment) {

    companion object {
        fun newInstance(): HeroesFragment {
            return HeroesFragment()
        }
    }

    private val viewModel: HeroesViewModel by activityViewModels()
    private lateinit var binding: HeroesFragmentBinding
    private lateinit var heroesAdapter: HeroesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HeroesFragmentBinding.bind(view)

        setupPagedListAdapter()
        setupListeners()
    }

    private fun setupListeners() {
        binding.bAddModule.setOnClickListener {
            val dialog = AvailableModuleDialog.newInstance()
            dialog.show(childFragmentManager, null)
        }
        viewModel.status.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    is HeroesViewModel.State.Data -> {
                        heroesAdapter.submitList(state.list)
                        binding.bAddModule.isVisible = state.isVisibleDownloadFeatureButton
                    }
                    is HeroesViewModel.State.FeatureState -> when (state.featureState) {
                        FeatureManager.FeatureState.Canceled -> Unit
                        FeatureManager.FeatureState.Error -> Unit
                        FeatureManager.FeatureState.Installed -> Unit
                        FeatureManager.FeatureState.Nothing -> Unit
                        is FeatureManager.FeatureState.Data -> Unit
                        is FeatureManager.FeatureState.Downloading -> Unit
                        is FeatureManager.FeatureState.RequiredInformation -> Unit
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    fun openModule(moduleName: String) {
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
            addItemDecoration(OffsetItemDecorator())
            adapter = heroesAdapter
        }
    }

}