package com.ytowka.worktimer2.screens.editor

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.adapters.ActionTypesAdapter
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionType
import com.ytowka.worktimer2.databinding.FragmentActionEditBinding
import com.ytowka.worktimer2.utils.C.Companion.getTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActionEditFragment : Fragment() {

    lateinit var binding: FragmentActionEditBinding
    lateinit var viewmodel: EditingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActionEditBinding.inflate(inflater)
        viewmodel = ViewModelProvider(requireActivity()).get(EditingViewModel::class.java)


        initViews(viewmodel.currAction)
        return binding.root
    }

    private fun initViews(action: Action) {
        initAction(action)

        binding.colorView.setOnClickListener {
            showColorPickerDialog()
        }

        binding.nameTextEdit.addTextChangedListener {
            viewmodel.setActionName(it.toString())
        }
        binding.editTextTextSS.addTextChangedListener {
            viewmodel.secondsEdit(it.toString())
        }
        binding.editTextTextMM.addTextChangedListener {
            viewmodel.minutesEdit(it.toString())
        }

        binding.toolbarActionEdit.menu.clear()
        binding.toolbarActionEdit.inflateMenu(R.menu.action_edit_menu)
        binding.toolbarActionEdit.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_edit_done -> {

                }
                R.id.action_edit_copy -> {
                    viewmodel.copyAction()
                }
                R.id.action_edit_delete -> {
                    viewmodel.deleteAction()
                }
            }
            findNavController().navigateUp()
            true
        }
        val onActionTypeClick: (ActionType) -> Unit = {
            initAction(viewmodel.applyActionType(it))
        }
        val onActionTypeDelete: (ActionType) -> Unit = {
            viewmodel.deleteActionType(it)
        }
        val adapter = ActionTypesAdapter(
            listOf(),
            onActionTypeClick,
            onActionTypeDelete,
            viewmodel.defaultTypes
        )
        binding.switch1.setOnClickListener {
            val state = (it as SwitchCompat).isChecked
            viewmodel.switchTimeMode(state)
            updateTimeMode(state)
        }
        binding.rvActionTypeList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvActionTypeList.adapter = adapter
        viewmodel.getActionTypes().observe(viewLifecycleOwner) {
            adapter.setUp(it)
        }
    }

    private fun initAction(action: Action) {
        binding.colorView.backgroundTintList = ColorStateList.valueOf(action.color)
        binding.switch1.isChecked = action.exactTimeDefine
        updateTimeMode(action.exactTimeDefine)
        binding.nameTextEdit.setText(action.name)
        binding.editTextTextMM.setText(action.getMinutesOfDuration().toString())
        var secondsString = action.getSecondsOfDuration().toString()
        if (secondsString.length == 1) {
            secondsString = "0$secondsString"
        }
        binding.editTextTextSS.setText(secondsString)
    }

    private fun updateTimeMode(exactTimeDefine: Boolean) {
        if (exactTimeDefine) {
            binding.editTextTextSS.isEnabled = true
            binding.editTextTextMM.isEnabled = true

            binding.imgActionEditReplay.visibility = View.INVISIBLE

            binding.editTextTextSS.visibility = View.VISIBLE
            binding.editTextTextMM.visibility = View.VISIBLE
            binding.sep.visibility = View.VISIBLE
        } else {
            binding.editTextTextSS.isEnabled = false
            binding.editTextTextMM.isEnabled = false

            binding.imgActionEditReplay.visibility = View.VISIBLE

            binding.editTextTextSS.visibility = View.INVISIBLE
            binding.editTextTextMM.visibility = View.INVISIBLE
            binding.sep.visibility = View.INVISIBLE
        }
    }

    private fun showColorPickerDialog() {
        val colorPickerDialog = ColorPickerDialogBuilder
            .with(context)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .initialColor(viewmodel.currAction.color)
            .setTitle(getString(R.string.choose_color))
            .density(9)
            .setPositiveButton(
                R.string.apply
            ) { d, lastSelectedColor, allColors ->
                viewmodel.changeColor(lastSelectedColor)
                binding.colorView.backgroundTintList = ColorStateList.valueOf(lastSelectedColor)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
            }
            .lightnessSliderOnly()
            .build()
        colorPickerDialog.show()
        colorPickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(resources.getColor(R.color.colorOnSurface, requireContext().theme))
        colorPickerDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(resources.getColor(R.color.colorOnSurface, requireContext().theme))


        val backgroundDrawable = getDrawable(resources, R.drawable.shape_rounded25, getTheme())
        backgroundDrawable?.setTint(resources.getColor(R.color.cardViewBg))
        colorPickerDialog.window?.setBackgroundDrawable(backgroundDrawable)
    }

    override fun onStop() {
        viewmodel.commitChanges()
        super.onStop()
    }
}