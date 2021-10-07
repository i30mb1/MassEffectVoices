package n7.mev.ui.heroes

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import androidx.transition.TransitionManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.mev.R
import n7.mev.databinding.HeroesFragmentBinding
import n7.mev.lazyUnsafe
import n7.mev.ui.heroes.adapter.HeroesAdapter
import n7.mev.ui.heroes.adapter.OffsetItemDecorator
import n7.mev.ui.heroes.vo.HeroVO
import n7.mev.ui.sounds.SoundsFragment

class HeroesFragment private constructor() : Fragment(R.layout.heroes_fragment) {

    companion object {
        fun newInstance(): HeroesFragment {
            return HeroesFragment()
        }
    }

    private val viewModel: HeroesViewModel by activityViewModels()
    private lateinit var binding: HeroesFragmentBinding
    private lateinit var heroesAdapter: HeroesAdapter
    private val offsetItemDecorator = OffsetItemDecorator()
    private val onHeroClickListener: (model: HeroVO) -> Unit = { model -> openModule(model.moduleName) }
    private val buttonAnimation by lazyUnsafe {
        Slide().apply {
            addTarget(binding.bAddModule)
        }
    }

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
//                        binding.bAddModule.isVisible = state.isVisibleDownloadFeatureButton
                    }
                    is HeroesViewModel.State.FeatureManagerState -> when (state.featureState) {
                        FeatureManager.State.Canceled -> Unit
                        FeatureManager.State.Error -> Unit
                        FeatureManager.State.Installed -> Unit
                        FeatureManager.State.Nothing -> Unit
                        is FeatureManager.State.Data -> Unit
                        is FeatureManager.State.Downloading -> Unit
                        is FeatureManager.State.RequiredInformation -> Unit
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun openModule(moduleName: String) {
        SoundsFragment.newInstance(moduleName)
        TransitionManager.beginDelayedTransition(binding.root, buttonAnimation)
        binding.bAddModule.isVisible = !binding.bAddModule.isVisible
        offsetItemDecorator.isExtraPaddingBot = binding.bAddModule.isVisible
        binding.rv.invalidateItemDecorations()
    }

    private fun setupPagedListAdapter() {
        heroesAdapter = HeroesAdapter(layoutInflater, onHeroClickListener)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addItemDecoration(offsetItemDecorator)
            adapter = heroesAdapter
        }
    }

}