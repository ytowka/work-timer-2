package com.ytowka.worktimer2.screens.fragments

import android.content.res.Resources
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.adapters.ActionListTouchHelperCallback
import com.ytowka.worktimer2.adapters.EditActionListAdapter
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.databinding.FragmentEditSetBinding
import com.ytowka.worktimer2.screens.viewmodels.EditSetViewModel
import com.ytowka.worktimer2.utils.C.Companion.toStringTime
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditSetFragment : Fragment() {
    private lateinit var binding: FragmentEditSetBinding
    private val args: EditSetFragmentArgs by navArgs()
    private lateinit var viewModel: EditSetViewModel

    private lateinit var adapter: EditActionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.complex)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditSetBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(EditSetViewModel::class.java)

        //if this fragment opened for new action set, setId = -1
        binding.toolbar2.title = requireContext().getString(if (args.setId == -1L) R.string.creating else R.string.editing)

        val typedValue = TypedValue()
        val theme: Resources.Theme = requireContext().theme
        theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
        @ColorInt val color = typedValue.data

        binding.toolbar2.setTitleTextColor(color)
        viewModel.initViewModel(args.setId).observe(viewLifecycleOwner){ set ->
            initViews(set)
        }
        viewModel.actionsLiveData.observe(viewLifecycleOwner){ actions ->
            adapter.setUp(actions)
        }

        val long: Long = 123123
        long.toStringTime()

        return binding.root

    }

    private fun initViews(actionSet: ActionSet){
        adapter = EditActionListAdapter(actionSet.actions.toMutableList(), viewModel)
        binding.rvEditingActionsList.layoutManager = LinearLayoutManager(context)
        binding.rvEditingActionsList.adapter = adapter
        binding.floatingActionButtonAddAction.setOnClickListener {
            viewModel.newAction(adapter.itemCount)
        }
        ItemTouchHelper(ActionListTouchHelperCallback(adapter)).attachToRecyclerView(binding.rvEditingActionsList)
    }
    override fun onStop() {
        viewModel.commitChanges()
        super.onStop()
    }
}