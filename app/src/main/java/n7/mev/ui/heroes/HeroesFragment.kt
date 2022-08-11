package n7.mev.ui.heroes

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.mev.Navigator
import n7.mev.R
import n7.mev.databinding.HeroesFragmentBinding
import n7.mev.lazyUnsafe
import n7.mev.ui.heroes.adapter.HeroesAdapter
import n7.mev.ui.heroes.adapter.OffsetItemDecorator
import n7.mev.ui.heroes.vo.HeroVO
import kotlin.math.roundToInt

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density.roundToInt())

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
        setupInsets()
    }

    private fun setupInsets() {
        val toolbarMarginTop = binding.toolbar.marginTop
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            binding.toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = toolbarMarginTop + statusBarsInsets.top + 1.dpToPx
            }
            binding.bAddModule.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = navigationBarsInsets.bottom
            }
            offsetItemDecorator.extraPaddingBot = navigationBarsInsets.bottom
            binding.rv.invalidateItemDecorations()
            insets
        }
    }

    private fun setupListeners() {
        binding.bAddModule.setOnClickListener { openAvailableModuleDialog() }
        viewModel.status.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    is HeroesViewModel.State.Data -> {
                        heroesAdapter.submitList(state.list) { binding.rv.invalidateItemDecorations() }
                        binding.bAddModule.isVisible = state.isVisibleDownloadFeatureButton
                        offsetItemDecorator.isBottomButtonVisible = state.isVisibleDownloadFeatureButton
                    }
                    is HeroesViewModel.State.FeatureManagerState -> when (state.featureState) {
                        FeatureManager.State.Canceled -> binding.pb.isVisible = false
                        FeatureManager.State.Error -> binding.pb.isVisible = false
                        FeatureManager.State.Installed -> binding.pb.isVisible = false
                        is FeatureManager.State.Downloading -> {
                            binding.pb.isVisible = true
                            binding.pb.progress = state.featureState.currentBytes
                            binding.pb.max = state.featureState.totalBytes
                        }
                        is FeatureManager.State.RequiredInformation -> viewModel.startConfirmationDialog(this, state.featureState.state)
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun openAvailableModuleDialog() {
        setFragmentResultListener(AvailableModuleDialog.AVAILABLE_MODULE_LISTENER_KEY) { _, result ->
            val moduleName = result.getString(AvailableModuleDialog.AVAILABLE_MODULE_LISTENER_RESULT)!!
            viewModel.installModule(moduleName)
        }
        val dialog = AvailableModuleDialog.newInstance()
        dialog.show(parentFragmentManager, null)
    }

    private fun openModule(moduleName: String) {
        (activity as Navigator).openSoundsFragment(moduleName)
    }

    private fun setupPagedListAdapter() {
        heroesAdapter = HeroesAdapter(layoutInflater, onHeroClickListener)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(offsetItemDecorator)
            itemAnimator = null
            adapter = heroesAdapter
        }
    }

}