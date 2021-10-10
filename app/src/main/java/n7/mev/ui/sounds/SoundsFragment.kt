package n7.mev.ui.sounds

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import n7.mev.R
import n7.mev.databinding.MainFragmentBinding
import n7.mev.main.MainViewModel
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

    private val mViewModel: MainViewModel by viewModels()
    private lateinit var soundsAdapter: SoundsAdapter
    private lateinit var binding: MainFragmentBinding
    private val onSoundClickListener: (model: SoundVO) -> Unit = { model -> Toast.makeText(context, "$model", Toast.LENGTH_SHORT).show()  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainFragmentBinding.bind(view)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        soundsAdapter = SoundsAdapter(layoutInflater, onSoundClickListener)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = soundsAdapter
        }
    }

}