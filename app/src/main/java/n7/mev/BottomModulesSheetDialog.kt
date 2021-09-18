package n7.mev

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import n7.mev.databinding.BottomDrawerBinding
import n7.mev.ui.heroes.HeroesViewModel
import java.util.*

class BottomModulesSheetDialog : BottomSheetDialogFragment() {

    private val viewModel: HeroesViewModel by viewModels()
    private lateinit var binding: BottomDrawerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomDrawerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val installedModules = viewModel.installedModules()
        val mapOfModules: Map<String, String> = ConvertFeaturesToMapUseCase().invoke(installedModules)

        mapOfModules.forEach {
            val item = binding.nav.menu.add(it.key)
            val resourceId = resources.getIdentifier(it.key.toLowerCase(Locale.ROOT), "drawable", requireContext().packageName)
            item.setIcon(resourceId)
        }
        binding.nav.setNavigationItemSelectedListener { item ->
            dismiss()
            viewModel.installModule(mapOfModules[item.title]!!)
            true
        }
    }

}