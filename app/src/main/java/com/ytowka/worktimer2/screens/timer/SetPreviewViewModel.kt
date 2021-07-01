package com.ytowka.worktimer2.screens.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.*
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.utils.C
import com.ytowka.worktimer2.utils.timers.ActionsTimerSequence
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetPreviewViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    @Inject
    lateinit var finishAction: Action

    var isAppOpened = false
    set(value) {
        field = value
        timerService?.isAppOpened = value
    }
    private var timerService: TimerService? = null
    fun getSetId() = timerService!!.setId

    var sound = true
    set(value) {
        field = value
        timerService?.sound = value
    }
    fun toggleSound(){
        sound = !sound
    }

    private val _actionSetLiveData = MutableLiveData<ActionSet>()
    val actionSetLiveData: LiveData<ActionSet> = _actionSetLiveData


    private val _nullSetLiveData = MutableLiveData<Unit>()
    val nullSetLiveData: LiveData<Unit> = _nullSetLiveData

    fun updateActionSet(){
        timerService!!.updateSet {
            _nullSetLiveData.value = Unit
        }
    }

    private val timerSequenceObserver = Observer<ActionsTimerSequence>  {
        _actionSetLiveData.value = it.actionSet
        C.log("viewmodel connected ${it.started}")
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, p1: IBinder?) {
            val serviceBinder = p1 as TimerService.MyBinder
            timerService = serviceBinder.service
            timerService!!.isAppOpened = isAppOpened
            timerService!!.timerSequenceLiveData.observeForever(timerSequenceObserver)
        }
        override fun onServiceDisconnected(p0: ComponentName?) {}
    }

    //fun returns true if button starts or resumes current timer
    fun clickStartBtn(): Boolean {
        if (!isStarted()) {
            start()
            return true
        } else {
            return if (currentAction().exactTimeDefine) {
                if (isTimerPaused()) {
                    resumeTimer()
                    true
                } else {
                    pauseTimer()
                    false
                }
            } else {
                nextTimer()
                true
            }
        }
    }
    fun isStarted() = timerService!!.isStarted()
    fun isRestarted() = timerService!!.isRestarted()

    fun setOnActionFinishCallback(callback: (Action) -> Unit) = timerService?.setOnActionFinishCallback(callback) ?: {throw Exception("timer service not inited")}
    fun setupCallbacks(progressBarUpdate: (Long) -> Unit, timeTextUpdate: (Long) -> Unit){
        timerService?.setupCallbacks(progressBarUpdate, timeTextUpdate)
    }

    fun currentAction() = timerService!!.currentAction()

    fun setOnSequenceFinish(onFinish: () -> Unit) {
        timerService!!.onSequenceFinish = onFinish
    }

    val paused: Boolean get() = timerService!!.paused

    fun jumpTo(action: Action) = timerService!!.jumpTo(action)

    fun getCurrentTimerTime(): Long = timerService!!.getCurrentTimerTime()

    private fun start() = timerService!!.startTimers()

    private fun nextTimer() = timerService!!.nextTimer()

    private fun isTimerPaused(): Boolean = timerService!!.isTimerPaused()


    fun stop(){
        timerService?.stopTimer()
    }


    fun pauseTimer() = timerService!!.pauseTimer()
    fun resumeTimer() = timerService!!.resumeTimer()

    override fun onCleared() {
        actionSetLiveData.value?.setInfo?.let {
            it.lastOpen = System.currentTimeMillis()
            GlobalScope.launch {
                repository.updateSetInfo(it)
            }
        }
        timerService!!.timerSequenceLiveData.removeObserver(timerSequenceObserver)
        super.onCleared()
    }
}