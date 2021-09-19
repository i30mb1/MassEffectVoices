package n7.mev.ui.heroes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import n7.mev.databinding.BottomDrawerBinding
import n7.mev.ui.heroes.adapter.HeroesSmallAdapter
import n7.mev.ui.heroes.adapter.OffsetItemDecorator
import n7.mev.ui.heroes.vo.HeroVO

class AvailableModuleDialog private constructor() : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = AvailableModuleDialog()
    }

    private lateinit var binding: BottomDrawerBinding
    private val viewModel: HeroesViewModel by activityViewModels()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomDrawerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
    }

    private fun setupRecycler() {
        val onHeroClickListener: (model: HeroVO) -> Unit = { }
        val heroesAdapter = HeroesSmallAdapter(layoutInflater, onHeroClickListener)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(OffsetItemDecorator())
            adapter = heroesAdapter
        }

        scope.launch {
            val list = viewModel.getReadyToInstallModules()
            heroesAdapter.submitList(list)
        }
    }

}