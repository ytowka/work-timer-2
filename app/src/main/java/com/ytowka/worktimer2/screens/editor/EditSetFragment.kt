package com.ytowka.worktimer2.screens.editor

import android.content.res.Resources
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.adapters.ActionListTouchHelperCallback
import com.ytowka.worktimer2.adapters.EditActionListAdapter
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.databinding.DialogEditingActionExitBinding
import com.ytowka.worktimer2.databinding.FragmentEditSetBinding
import com.ytowka.worktimer2.utils.C
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditSetFragment : Fragment() {
    private lateinit var binding: FragmentEditSetBinding
    private lateinit var viewModel: EditingViewModel
    private var setId: Long = 0

    private lateinit var adapter: EditActionListAdapter

    private lateinit var setName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.complex)
    }

    private val backPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            buildDialog {
                binding.progressBarSavingAction.visibility = View.VISIBLE
                viewModel.commitChanges().observe(viewLifecycleOwner){
                    (requireActivity() as EditorActivity).exit()
                }
            }.apply {
                show()
                window?.setBackgroundDrawableResource(R.drawable.dialog_bg);
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditSetBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity()).get(EditingViewModel::class.java)
        setId = (requireActivity() as EditorActivity).args.setId

        //if this fragment opened for new action set, setId = -1
        binding.toolbar2.title = requireContext().getString(if (setId == -1L) R.string.creating else R.string.editing)

        val typedValue = TypedValue()
        val theme: Resources.Theme = requireContext().theme
        theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
        @ColorInt val color = typedValue.data
        //val color = getColor(R.color.colorOnPrimary, getTheme())

        binding.toolbar2.setTitleTextColor(color)
        val openEditActionFragment: (Action) -> Unit = { action ->
            val transitionAction =
                EditSetFragmentDirections.toActionEdit()
            viewModel.currAction = action
            findNavController().navigate(transitionAction)
        }


        viewModel.initViewModel(setId).observe(viewLifecycleOwner){
            initViews()

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,backPressedCallback);
        }
        adapter = EditActionListAdapter(
            mutableListOf(),
            openEditActionFragment
            , viewModel)
        viewModel.infoLiveData.observe(viewLifecycleOwner){
            setName = it.name
        }
        viewModel.actionsLiveData.observe(viewLifecycleOwner){ actions ->
            adapter.setUp(actions)
            /*if(actions.isNotEmpty()){
                viewModel.currAction = actions[0]
                binding.rvEditingActionsList.scrollToPosition(actions.indexOf(viewModel.currAction))
            }*/
        }
        return binding.root
    }
    private fun initViews(){
        binding.rvEditingActionsList.layoutManager = LinearLayoutManager(context)
        binding.rvEditingActionsList.adapter = adapter
        binding.floatingActionButtonAddAction.setOnClickListener {
            viewModel.newAction(adapter.itemCount)
        }
        ItemTouchHelper(ActionListTouchHelperCallback(adapter)).attachToRecyclerView(binding.rvEditingActionsList)
    }

    private fun buildDialog(onConfirm: () -> Unit): AlertDialog {
        return requireActivity().let {
            val builder = AlertDialog.Builder(it,R.style.roundedDialog)
            val dialogBinding = DialogEditingActionExitBinding.inflate(requireActivity().layoutInflater)
            builder.setView(dialogBinding.root)
            val dialog = builder.create()
            dialogBinding.edittextTimerName.setText(setName)
            dialogBinding.buttonConfirm.setOnClickListener {
                val symbols = dialogBinding.edittextTimerName.text.toString().length
                if(symbols > 0){
                    onConfirm()
                    dialog.dismiss()
                }else{
                    dialogBinding.edittextTimerName.error = getString(R.string.empty_name)
                }

            }
            dialogBinding.buttonCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.edittextTimerName.addTextChangedListener{ text ->
                viewModel.updateSetName(text.toString())
            }
            dialog
        }
    }
}