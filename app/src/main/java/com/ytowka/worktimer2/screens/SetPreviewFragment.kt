package com.ytowka.worktimer2.screens

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.adapters.PreviewActionListAdapter
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.databinding.FragmentSetPreviewBinding
import com.ytowka.worktimer2.utils.C.Companion.TAG
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

fun Long.toStringTime(): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(minutes)
    var string = ""
    if (minutes != 0L) {
        string += "$minutes:"
        if (seconds < 10) string += 0
    }
    string += seconds
    return string
}

@AndroidEntryPoint
class SetPreviewFragment : Fragment() {
    private lateinit var binding: FragmentSetPreviewBinding
    private lateinit var viewmodel: SetPreviewViewModel


    private val args: SetPreviewFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewmodel = ViewModelProvider(this).get(SetPreviewViewModel::class.java)
        viewmodel.setup(args.setId)
        binding = FragmentSetPreviewBinding.inflate(inflater)

        binding.cardView.transitionName = "frame${args.setId}"
        binding.textSetNameTitle.transitionName = "name${args.setId}"
        binding.btnPStart.transitionName = "time${args.setId}"

        val dialog = buildDialog()

        val backPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(viewmodel.isStarted()){
                    viewmodel.pauseTimer()
                    setPauseButtonIcon(false)
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(resources.getColor(R.color.colorOnSurface,requireContext().theme))
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(resources.getColor(R.color.colorOnSurface,requireContext().theme))
                    dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg);
                }else{
                    findNavController().navigateUp()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,backPressedCallback);

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.complex)
        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onStart()
        binding.rvPActionList.layoutManager = LinearLayoutManager(context)

        val adapter = PreviewActionListAdapter {
            if (viewmodel.isStarted()) {
                viewmodel.jumpTo(it)
            }
        }
        binding.rvPActionList.adapter = adapter

        viewmodel.getSet().observe(viewLifecycleOwner) {
            binding.textSetNameTitle.text = it.setInfo.name
            binding.textTimeOnButton.text = it.getStringDuration()
            binding.currentActionTime.text = (it.actions.first().duration.toLong() * 1000).toStringTime()
            adapter.setup(it.actions)

            if (viewmodel.isStarted()) {
                binding.motionLayoutPreview.setState(R.id.endCS, ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.MATCH_CONSTRAINT)
                binding.motionLayoutPreview.progress = 1.0f
                setPauseButtonIcon(viewmodel.paused || viewmodel.currentAction().exactTimeDefine)
            }
            initCard(viewmodel.currentAction())
            if (viewmodel.isStarted()) adapter.currentAction = viewmodel.currentAction()

            binding.btnPStart.setOnClickListener {
                if (viewmodel.clickStartBtn()) {
                    binding.motionLayoutPreview.transitionToEnd()
                }
            }
            binding.pauseButton.setOnClickListener {
                val launchFlag = viewmodel.clickStartBtn()
                setPauseButtonIcon(launchFlag)
                if(!viewmodel.currentAction().exactTimeDefine){
                    setPauseButtonIcon(false)
                }
                if(viewmodel.currentAction() == viewmodel.finishAction){
                    binding.pauseButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_replay))
                }
                if(viewmodel.isRestarted()){
                    setPauseButtonIcon(false)
                }
            }
            binding.currentActionName.text = it.actions.first().name

            viewmodel.setupCallbacks(
                    progressBarUpdate =
                    { time ->
                        binding.progressBar2.progress = time.toInt()
                    }, timeTextUpdate =
            { time ->
                binding.currentActionTime.text = time.toStringTime()
            })
            viewmodel.setOnActionFinishCallback { action ->
                initCard(action)
                binding.progressBar2.progress = binding.progressBar2.max
                adapter.currentAction = action
                setPauseButtonIcon(action.exactTimeDefine)
                binding.currentActionTime.text = if (action.exactTimeDefine) (action.duration * 1000).toLong().toStringTime() else "0"
            }
            viewmodel.setOnSequenceFinish {
                adapter.currentAction = null
                binding.rvPActionList.scrollToPosition(0)
                binding.pauseButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_arrow))
            }
            startPostponedEnterTransition()
        }
        binding.btnPEdit.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.btnPEdit to "edit")
            val action = SetPreviewFragmentDirections.editSetFromPreview(args.setId)
            findNavController().navigate(action, extras)
        }
    }

    private fun initCard(action: Action) {
        val isLight = let {
            val r = Color.red(action.color)
            val g = Color.green(action.color)
            val b = Color.blue(action.color)
            (r + g + b) / 3 >= 135
        }

        binding.progressBar2.max = action.duration * 1000
        binding.progressBar2.progress = viewmodel.getCurrentTimerTime().toInt()
        binding.currentActionTime.text = if (action.exactTimeDefine) viewmodel.getCurrentTimerTime().toStringTime() else "0"

        binding.currentActionTime.backgroundTintList = ColorStateList.valueOf(action.color)
        binding.currentActionName.backgroundTintList = ColorStateList.valueOf(action.color)
        binding.pauseButton.backgroundTintList = ColorStateList.valueOf(action.color)
        binding.progressBar2.progressTintList = ColorStateList.valueOf(action.color)

        binding.currentActionName.setTextColor(ContextCompat.getColor(requireContext(), if (isLight) R.color.on_light else R.color.on_dark))
        binding.currentActionTime.setTextColor(ContextCompat.getColor(requireContext(), if (isLight) R.color.on_light else R.color.on_dark))
        binding.pauseButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), if (isLight) R.color.on_light else R.color.on_dark))

        binding.currentActionName.text = action.name
    }
    private fun setPauseButtonIcon(pause: Boolean){
        binding.pauseButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), if (pause) R.drawable.ic_pause else R.drawable.ic_play_arrow))
    }
    private fun buildDialog(): AlertDialog{
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it,R.style.roundedDialog)
        }
        builder?.setOnCancelListener {
            viewmodel.resumeTimer()
            setPauseButtonIcon(true)
        }
        builder?.setMessage(R.string.timer_exit_confirmation)
        builder?.apply {
            setPositiveButton(R.string.cancel
            ) { dialog, id ->
                viewmodel.resumeTimer()
                setPauseButtonIcon(true)
            }
            setNegativeButton(R.string.exit
            ) { dialog, id ->
                viewmodel.stopTimer()
                findNavController().navigateUp()
            }
        }
        val dialog: AlertDialog? = builder?.create()
        return dialog!!
    }
}