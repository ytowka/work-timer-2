package com.ytowka.worktimer2.screens.timer

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.adapters.PreviewActionListAdapter
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.databinding.FragmentSetPreviewBinding
import com.ytowka.worktimer2.utils.C
import com.ytowka.worktimer2.utils.C.Companion.toStringTime
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SetPreviewFragment : Fragment() {
    private lateinit var binding: FragmentSetPreviewBinding
    private lateinit var viewmodel: SetPreviewViewModel

    private val args: SetPreviewFragmentArgs by navArgs()
    private var setId = 0L

    //if user clicks on edit button this field sets to true, so next time user returns to this screen
    //from editing screen fragment says to viewmodel that actionSet have to be updated
    private var readyForUpdateFlag: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetPreviewBinding.inflate(inflater)
        viewmodel = ViewModelProvider(this).get(SetPreviewViewModel::class.java)
        setId = args.setId

        viewmodel.actionSetLiveData.observe(viewLifecycleOwner){
            setId = viewmodel.getSetId()
            initViews(it)
        }
        binding.previewToolbar.inflateMenu(R.menu.preview_menu)

        binding.previewToolbar.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.volumeButton ->{
                    viewmodel.toggleSound()
                    val iconResource = if(viewmodel.sound) R.drawable.ic_volume_up else R.drawable.ic_volume_off
                    binding.previewToolbar.menu.getItem(0).setIcon(iconResource)
                }
            }
            true
        }

        binding.cardView.transitionName = "frame$setId"
        binding.textSetNameTitle.transitionName = "name$setId"
        binding.btnPStart.transitionName = "time$setId"

        val dialog = buildDialog()

        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewmodel.isStarted()) {
                    viewmodel.pauseTimer()
                    setPauseButtonIcon(false)
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
                    dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg);
                } else {
                    stopService()
                    findNavController().navigateUp()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        );
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transition = TransitionInflater.from(context).inflateTransition(R.transition.complex)
        sharedElementEnterTransition = transition
        postponeEnterTransition()
    }



    // `true`  for resumed state
    // `false` for paused state
    private fun setPauseButtonIcon(pause: Boolean) {
        binding.pauseButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (pause) R.drawable.ic_pause else R.drawable.ic_play_arrow
            )
        )
    }

    private fun buildDialog(): AlertDialog {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it, R.style.roundedDialog)
        }
        builder?.setOnCancelListener {
            viewmodel.resumeTimer()
            setPauseButtonIcon(true)
        }
        builder?.setMessage(R.string.timer_exit_confirmation)
        builder?.apply {
            setPositiveButton(
                R.string.cancel
            ) { dialog, id ->
                viewmodel.resumeTimer()
                setPauseButtonIcon(true)
            }
            setNegativeButton(
                R.string.exit
            ) { dialog, id ->
                stopService()
                findNavController().navigateUp()
            }
        }
        val dialog: AlertDialog? = builder?.create()
        return dialog!!
    }

    private fun initViews(actionSet: ActionSet) {
        binding.rvPActionList.layoutManager = LinearLayoutManager(context)
        val adapter = PreviewActionListAdapter {
            if (viewmodel.isStarted()) {
                viewmodel.jumpTo(it)
            }
        }
        val iconResource = if(viewmodel.sound) R.drawable.ic_volume_up else R.drawable.ic_volume_off
        binding.previewToolbar.menu.getItem(0).setIcon(iconResource)

        if (viewmodel.isStarted()) {
            binding.motionLayoutPreview.transitionToEnd()
            binding.motionLayoutPreview.progress = 1.0f

            setPauseButtonIcon(!viewmodel.paused)
            if(!viewmodel.currentAction().exactTimeDefine){
                setPauseButtonIcon(false)
            }
            adapter.currentAction = viewmodel.currentAction()
        }

        binding.rvPActionList.adapter = adapter
        binding.btnPEdit.setOnClickListener {
            readyForUpdateFlag = true
            val extras = FragmentNavigatorExtras(binding.btnPEdit to "edit")
            val action = SetPreviewFragmentDirections.editSetFromPreview(setId)
            findNavController().navigate(action, extras)
        }


        binding.textSetNameTitle.text = actionSet.setInfo.name
        Log.i("debug", actionSet.setInfo.name)
        binding.textTimeOnButton.text = actionSet.getStringDuration()
        binding.currentActionTime.text = (actionSet.actions.first().duration * 1000).toStringTime()

        adapter.setup(actionSet.actions)

        initCard(viewmodel.currentAction())

        binding.btnPStart.setOnClickListener {
            if (viewmodel.clickStartBtn()) {
                binding.motionLayoutPreview.transitionToEnd()
            }
        }
        binding.pauseButton.setOnClickListener {
            val launchFlag = viewmodel.clickStartBtn()
            setPauseButtonIcon(launchFlag)
            if (!viewmodel.currentAction().exactTimeDefine) {
                setPauseButtonIcon(false)
            }
            if (viewmodel.currentAction() == viewmodel.finishAction) {
                binding.pauseButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_replay
                    )
                )
            }
            if (viewmodel.isRestarted()) {
                setPauseButtonIcon(false)
            }
        }
        binding.currentActionName.text = actionSet.actions.first().name

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
            binding.currentActionTime.text =
                if (action.exactTimeDefine) (action.duration * 1000).toStringTime() else "0"
        }
        viewmodel.setOnSequenceFinish {
            adapter.currentAction = null
            binding.rvPActionList.scrollToPosition(0)
            binding.pauseButton.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_play_arrow
                )
            )
        }
        startPostponedEnterTransition()
    }

    private fun initCard(action: Action) {
        val isLight = C.isColorLight(action.color)

        binding.progressBar2.max = (action.duration.toLong() * 1000).toInt()
        binding.progressBar2.progress = viewmodel.getCurrentTimerTime().toInt()
        binding.currentActionTime.text =
            if (action.exactTimeDefine) viewmodel.getCurrentTimerTime().toStringTime() else "0"

        binding.currentActionTime.backgroundTintList = ColorStateList.valueOf(action.color)
        binding.currentActionName.backgroundTintList = ColorStateList.valueOf(action.color)
        binding.pauseButton.backgroundTintList = ColorStateList.valueOf(action.color)
        binding.progressBar2.progressTintList = ColorStateList.valueOf(action.color)

        binding.currentActionName.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isLight) R.color.on_light else R.color.on_dark
            )
        )
        binding.currentActionTime.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isLight) R.color.on_light else R.color.on_dark
            )
        )
        binding.pauseButton.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (isLight) R.color.on_light else R.color.on_dark
            )
        )

        binding.currentActionName.text = action.name
    }

    override fun onStart() {
        val serviceStartIntent = Intent(context, TimerService::class.java)
        requireContext().startService(serviceStartIntent)

        val serviceIntent = Intent(context, TimerService::class.java)

        serviceIntent.putExtra(C.EXTRA_SET_ID, setId)
        serviceIntent.action = C.ACTION_INIT_TIMER

        requireContext().bindService(
            serviceIntent,
            viewmodel.serviceConnection,
            Context.BIND_AUTO_CREATE
        )
        viewmodel.isAppOpened = true
        super.onStart()
    }

    override fun onResume() {
        if(readyForUpdateFlag){
            readyForUpdateFlag = false
            viewmodel.updateActionSet()
        }

        super.onResume()
    }

    fun stopService(){
        viewmodel.stop()
        requireContext().unbindService(viewmodel.serviceConnection)
    }

    override fun onStop() {
        viewmodel.isAppOpened = false
        super.onStop()
    }
}