package com.ytowka.worktimer2.screens.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.*
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.database.SetDao
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
    @ApplicationContext val context: Context
) : ViewModel() {

    @Inject
    lateinit var finishAction: Action

    var isAppOpened = false
    set(value) {
        field = value
        timerService?.isAppOpened = value
    }

    var serviceInited = false
    private set

    private var setId = -1L

    private var timerService: TimerService? = null

    private var actionSetLiveData = MutableLiveData<ActionSet>()

    private val timerSequenceObserver = Observer<ActionsTimerSequence>  {
        if(it != null){
            actionSetLiveData.value = it.actionSet
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val serviceBinder = p1 as TimerService.MyBinder
            timerService = serviceBinder.service
            serviceInited = true
            timerService!!.isAppOpened = isAppOpened
            timerService!!.timerSequenceLiveData.observeForever(timerSequenceObserver)
        }
        override fun onServiceDisconnected(p0: ComponentName?) {
            timerService = null
        }
    }
    fun setup(setId: Long): LiveData<ActionSet>{
        if(this.setId == -1L){
            val intentService = Intent(context, TimerService::class.java)
            intentService.putExtra(C.EXTRA_SET_ID, setId)
            intentService.action = C.ACTION_INIT_TIMER
            context.bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE)
            this.setId = setId
        }
        return actionSetLiveData
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
    fun setupCallbacks(progressBarUpdate: (Long) -> Unit, timeTextUpdate: (Long) -> Unit) = timerService?.setupCallbacks(progressBarUpdate,timeTextUpdate) ?: {throw Exception("timer service not inited")}

    fun currentAction() = timerService!!.currentAction()
    fun setOnSequenceFinish(onFinish: () -> Unit) = timerService!!.setOnSequenceFinish(onFinish)
    val paused: Boolean get() = timerService!!.paused
    fun jumpTo(action: Action) = timerService!!.jumpTo(action)
    fun getCurrentTimerTime(): Long = timerService!!.getCurrentTimerTime()
    private fun start() = timerService!!.startTimers()
    private fun nextTimer() = timerService!!.nextTimer()
    private fun isTimerPaused(): Boolean = timerService!!.isTimerPaused()
    fun stopTimer() {
        timerService?.stopSelf()
        context.unbindService(serviceConnection)
        timerService!!.stopTimer()
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