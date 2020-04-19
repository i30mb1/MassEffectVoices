package n7.mev

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import n7.mev.databinding.BottomDrawerBinding
import n7.mev.modules.ModulesViewModel

class BottomModulesSheetDialog : BottomSheetDialogFragment() {

    private val viewModel: ModulesViewModel by navGraphViewModels(R.id.nav_graph)
    private lateinit var binding: BottomDrawerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_drawer, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val installedModules = viewModel.installedModules()
        installedModules.forEach {
            binding.nav.menu.add(it.featureName)
        }
        binding.nav.setNavigationItemSelectedListener { item ->
            dismiss()
            viewModel.installModule(item.title.toString())
            true
        }
    }

}