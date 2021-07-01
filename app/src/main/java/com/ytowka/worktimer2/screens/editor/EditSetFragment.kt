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
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditSetFragment : Fragment() {
    private var _binding: FragmentEditSetBinding? = null
    private val binding: FragmentEditSetBinding
    get() = _binding!!


    private lateinit var viewmodel: EditingViewModel

    // if setId is -1 this set is new
    private var setId: Long = 0

    private lateinit var adapter: EditActionListAdapter

    private lateinit var setName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.complex)
    }

    fun onBackPressed(){
        if(viewmodel.isChanged){
            if(viewmodel.isEmpty){
                if(setId != -1L){
                    buildDeleteDialog {
                        viewmodel.deleteSet().observe(viewLifecycleOwner){
                            (requireActivity() as EditorActivity).exit()
                        }
                    }.apply {
                        show()
                        getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                            resources.getColor(
                                R.color.colorOnSurface,
                                requireContext().theme
                            )
                        )
                        getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                            resources.getColor(
                                R.color.colorOnSurface,
                                requireContext().theme
                            )
                        )
                        window?.setBackgroundDrawableResource(R.drawable.dialog_bg);
                    }
                }else{
                    (requireActivity() as EditorActivity).exit()
                }
            }else{
                buildNamingDialog {
                    binding.progressBarSavingAction.visibility = View.VISIBLE
                    viewmodel.commitChanges().observe(viewLifecycleOwner){
                        (requireActivity() as EditorActivity).exit()
                    }
                }.apply {
                    show()
                    window?.setBackgroundDrawableResource(R.drawable.dialog_bg);
                }
            }
        }else{
            (requireActivity() as EditorActivity).exit()
        }
    }
    private val backPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditSetBinding.inflate(inflater)
        viewmodel = ViewModelProvider(requireActivity()).get(EditingViewModel::class.java)
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
            viewmodel.currAction = action
            findNavController().navigate(transitionAction)
        }


        viewmodel.initViewModel(setId).observe(viewLifecycleOwner){
            initViews()
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,backPressedCallback);
        }
        adapter = EditActionListAdapter(
            mutableListOf(),
            openEditActionFragment
            , viewmodel)
        viewmodel.infoLiveData.observe(viewLifecycleOwner){
            setName = it.name
        }
        viewmodel.actionsLiveData.observe(viewLifecycleOwner){ actions ->
            binding.toolbar2.menu.clear()

            if(actions.isNotEmpty()){
                binding.toolbar2.inflateMenu(R.menu.set_edit_menu)
                binding.toolbar2.setOnMenuItemClickListener {
                    if(it.itemId == R.id.set_edit_done){
                        onBackPressed()
                    }
                    if(it.itemId == R.id.set_edit_delete){
                        val dialog = buildDeleteDialog{
                            viewmodel.deleteSet().observe(viewLifecycleOwner){
                                (requireActivity() as EditorActivity).exit()
                            }
                        }
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                            resources.getColor(
                                R.color.colorOnSurface,
                                requireContext().theme
                            )
                        )
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                            resources.getColor(
                                R.color.colorOnSurface,
                                requireContext().theme
                            )
                        )
                        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)
                    }
                    true
                }
            }

            adapter.setUp(actions)
        }
        return binding.root
    }
    private fun initViews(){
        binding.rvEditingActionsList.layoutManager = LinearLayoutManager(context)
        binding.rvEditingActionsList.adapter = adapter
        binding.floatingActionButtonAddAction.setOnClickListener {
            viewmodel.newAction(adapter.itemCount)
        }
        ItemTouchHelper(ActionListTouchHelperCallback(adapter)).attachToRecyclerView(binding.rvEditingActionsList)
    }

    private fun buildNamingDialog(onConfirm: () -> Unit): AlertDialog {
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
                viewmodel.updateSetName(text.toString())
            }
            dialog
        }
    }
    private fun buildDeleteDialog(onConfirm: () -> Unit): AlertDialog {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it, R.style.roundedDialog)
        }
        builder?.setMessage(R.string.delete_set)

        builder?.apply {
            setPositiveButton(
                R.string.cancel
            ) { _, _ -> }
            setNegativeButton(
                R.string.delete
            ) { _, _ ->
                onConfirm()
            }
        }
        val dialog: AlertDialog? = builder?.create()
        return dialog!!
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}