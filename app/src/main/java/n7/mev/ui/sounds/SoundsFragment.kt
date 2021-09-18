package n7.mev.ui.sounds

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.mev.R
import n7.mev.databinding.MainFragmentBinding
import n7.mev.main.MainViewModel
import n7.mev.main.SoundPagedListAdapter

class SoundsFragment : Fragment(R.layout.main_fragment) {

    companion object {
        fun newInstance(): SoundsFragment {
            return SoundsFragment()
        }
    }

    private val mViewModel: MainViewModel by viewModels()
    private val LAST_VISIBLE_ITEM = "LAST_VISIBLE_ITEM"
    private val LAST_SELECTED_HERO = "LAST_SELECTED_HERO"
    private lateinit var binding: MainFragmentBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: SoundPagedListAdapter
    private var lastVisibleItem = 0
    private lateinit var moduleName: String


    private fun setupToolbar() {
//        (activity as MainActivity?)!!.setSupportActionBar(binding.toolbar)
//        requireActivity().title = ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            lastVisibleItem = savedInstanceState.getInt(LAST_VISIBLE_ITEM)
            moduleName = savedInstanceState.getString(LAST_SELECTED_HERO)!!
        }
//        binding = MainFragmentBinding.bind(view).apply {
//            this.viewModel = mViewModel
//        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        mViewModel.createPagedListData(lastVisibleItem).observe(viewLifecycleOwner, Observer {
//            soundModels -> adapter.submitList(soundModels)
//        })

//        int resourceId = getResources().getIdentifier(moduleName, "drawable", getActivity().getPackageName());
//        binding.ivMainFragmentHero.setImageResource(resourceId);
        setupToolbar()
        setupRecyclerView()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lastVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
        outState.putInt(LAST_VISIBLE_ITEM, lastVisibleItem)
        outState.putString(LAST_SELECTED_HERO, moduleName)
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.rvMainFragment
        linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        adapter = SoundPagedListAdapter(mViewModel)
        recyclerView.adapter = adapter
    }

}