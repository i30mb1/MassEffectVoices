package n7.mev.ui.sounds

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import n7.mev.R
import n7.mev.databinding.MainFragmentBinding
import n7.mev.lazyUnsafe
import n7.mev.ui.sounds.adapter.SoundsAdapter
import n7.mev.ui.sounds.vo.SoundVO

class SoundsFragment private constructor() : Fragment(R.layout.main_fragment) {

    companion object {
        private const val MODULE_NAME = "MODULE_NAME"
        fun newInstance(moduleName: String) = SoundsFragment().apply {
            arguments = bundleOf(
                MODULE_NAME to moduleName
            )
        }
    }

    private val soundViewModel: SoundViewModel by viewModels()
    private lateinit var soundsAdapter: SoundsAdapter
    private lateinit var binding: MainFragmentBinding
    private val moduleName: String by lazyUnsafe { requireArguments().getString(MODULE_NAME)!! }
    private val onSoundClickListener: (model: SoundVO) -> Unit = { model -> Toast.makeText(context, "$model", Toast.LENGTH_SHORT).show() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainFragmentBinding.bind(view)

        setupRecyclerView()
        setupState()
    }

    private fun setupState() {
        soundViewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SoundViewModel.Companion.State.Data -> {
                    soundsAdapter.submitList(state.list)
                    binding.pb.isVisible = false
                }
                SoundViewModel.Companion.State.Loading -> binding.pb.isVisible = true
            }
        }
    }

    private fun setupRecyclerView() {
        soundsAdapter = SoundsAdapter(layoutInflater, onSoundClickListener)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = soundsAdapter
        }

        soundViewModel.load(moduleName)
    }

}