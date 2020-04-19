package n7.mev.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import n7.mev.R
import n7.mev.databinding.MainFragmentBinding
import n7.mev.modules.ModulesFragment

class MainFragment : Fragment(R.layout.main_fragment) {
    private val mViewModel: MainViewModel by viewModels {
        MainViewModelFactory(requireActivity().application, moduleName)
    }
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
        } else {
            moduleName = requireActivity().intent.getStringExtra(ModulesFragment.Companion.MODULE_NAME)
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
        binding.viewModel = mViewModel
        binding.executePendingBindings()

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

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}