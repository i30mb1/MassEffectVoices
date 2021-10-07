package n7.mev.ui.sounds

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import n7.mev.R
import n7.mev.databinding.MainFragmentBinding
import n7.mev.main.MainViewModel

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
    private lateinit var binding: MainFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.rvMainFragment
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

}