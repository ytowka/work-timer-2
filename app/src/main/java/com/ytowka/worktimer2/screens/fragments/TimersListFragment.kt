package com.ytowka.worktimer2.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.adapters.SetListAdapter
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.databinding.FragmentTimersListBinding
import com.ytowka.worktimer2.screens.viewmodels.TimerListViewModel
import com.ytowka.worktimer2.screens.TimersListFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class TimersListFragment : Fragment() {

    fun List<ActionSet>.filterActions(query: String): List<ActionSet> {
        val lowCaseQuery = query.toLowerCase(Locale.ROOT)
        val filteredList = mutableListOf<ActionSet>()
        forEach {
            val lowerCaseText = it.setInfo.name.toLowerCase(Locale.ROOT)
            if (lowerCaseText.contains(lowCaseQuery)) {
                filteredList.add(it)
            }
        }
        return filteredList
    }

    private lateinit var binding: FragmentTimersListBinding
    private lateinit var viewModel: TimerListViewModel

    private lateinit var setListAdapter: SetListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimersListBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(TimerListViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onStart()
        setListAdapter = SetListAdapter()
        setListAdapter.onItemClicked = { set, binding ->
            if (viewModel.selectingMode) {
                setListAdapter.checkItem(set)
                if (setListAdapter.checkedItemsCount() == 0) {
                    setEditingMode(false)
                } else {
                    setToolbarEditTitle(setListAdapter.checkedItemsCount())
                }
            } else {
                val setId = set.setInfo.setId
                val extras = FragmentNavigatorExtras(
                    binding.textSetName to "name$setId",
                    binding.textApproximateTime to "time$setId",
                    binding.cardSetInfo to "frame$setId",
                )
                val action =
                    TimersListFragmentDirections.previewSet(set.setInfo.setId, set.setInfo.name)
                findNavController().navigate(action, extras)
            }
        }
        setListAdapter.onItemLongClicked = {
            setEditingMode(true)
            setListAdapter.checkItem(it, true)
        }
        binding.listSets.apply {
            adapter = setListAdapter
            layoutManager = LinearLayoutManager(context)
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }

        binding.toolbar.menu.clear()
        binding.toolbar.inflateMenu(R.menu.setlist)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.app_bar_search -> {

                }
                R.id.appbar_cancel -> {
                    setEditingMode(false)
                }
                R.id.appbar_delete -> {
                    viewModel.deleteSets(setListAdapter.getCheckedActions())
                    setEditingMode(false)
                }
            }
            true
        }
        val searchItem = binding.toolbar.menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val list = viewModel.getSetList().value
                if (list != null) setListAdapter.update(list.filterActions(p0 ?: ""))
                binding.listSets.scrollToPosition(0)
                return true
            }
        })
        binding.FabAddSet.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.FabAddSet to "edit")
            val action = TimersListFragmentDirections.editSet(-1)
            viewModel.addSetItem()
            findNavController().navigate(action, extras)
        }
        viewModel.getSetList().observe(viewLifecycleOwner) {
            if (!viewModel.selectingMode) {
                setListAdapter.update(it)
            }
        }
    }

    private fun setEditingMode(editingMode: Boolean) {
        binding.toolbar.menu.clear()
        if (editingMode) {
            setToolbarEditTitle(1)
            binding.toolbar.inflateMenu(R.menu.setlist_edit)
            binding.FabAddSet.animate().translationX(350f)
            viewModel.selectingMode = true
        } else {
            binding.toolbar.title = resources.getString(R.string.app_name)
            binding.toolbar.inflateMenu(R.menu.setlist)
            viewModel.selectingMode = false
            binding.FabAddSet.animate().translationX(0f)
            setListAdapter.unCheckAll()
        }
    }

    private fun setToolbarEditTitle(selectedCount: Int) {
        binding.toolbar.title = resources.getString(R.string.selected) + ": " + selectedCount
    }


}