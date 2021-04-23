package com.ytowka.worktimer2.screens

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.database.SetDao
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.data.models.SetInfo
import com.ytowka.worktimer2.utils.ActionTimer
import com.ytowka.worktimer2.utils.ActionsTimerSequence
import com.ytowka.worktimer2.utils.Timer
import com.ytowka.worktimer2.utils.observeOnce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SetPreviewViewModel @Inject constructor(private val repository: Repository,private val setDao: SetDao) : ViewModel() {

    @Inject
    lateinit var finishAction: Action

    private var setId: Int? = null
    private lateinit var actionSet: ActionSet

    private lateinit var timerSequence: ActionsTimerSequence
    fun isStarted() = timerSequence.started
    fun isRestarted() = timerSequence.restarted

    fun setup(setId: Int) {
        if(this.setId == null){
            this.setId = setId
            getSet().observeOnce {
                actionSet = it.withFinishAction(finishAction)
                timerSequence = ActionsTimerSequence(actionSet)
            }
        }
    }
    fun setOnActionFinishCallback(callback: (Action)->Unit){
        timerSequence.onActionFinished = {
            callback(it.action)
            if(it.reverseCountDown){
                it.addCallback(13,progressBarUpdate)
            }else{
                it.addCallback(13) {time ->
                    progressBarUpdate((it.action.duration*1000).toLong())
                }
            }
            it.addCallback(1000,timeTextUpdate)
        }
    }
    fun currentAction() = timerSequence.currAction()

    private lateinit var progressBarUpdate: (Long) -> Unit
    private lateinit var timeTextUpdate: (Long) -> Unit

    fun setupCallbacks(progressBarUpdate: (Long) -> Unit, timeTextUpdate: (Long) -> Unit){
        this.progressBarUpdate = progressBarUpdate
        this.timeTextUpdate = timeTextUpdate
        timerSequence.currentTimer().clearCallBacks()
        val it = timerSequence.currentTimer()
        if(it.reverseCountDown){
            it.addCallback(13,progressBarUpdate)
        }else{
            it.addCallback(13) {time ->
                progressBarUpdate((it.action.duration*1000).toLong())
            }
        }
        it.addCallback(1000,timeTextUpdate)
    }
    fun getCurrentTimerTime(): Long{
        return timerSequence.currentTimer().msPassed
    }


    fun setOnSequenceFinish(onFinish: () -> Unit){
        timerSequence.onSequenceFinish = onFinish
    }

    fun jumpTo(action: Action){
        timerSequence.jumpToAction(action)
    }
    private fun getSetId(): Int {
        if (setId != null) return setId!!
        else throw Exception("setId must be initialized")
    }

    fun getSet(): LiveData<ActionSet> {
        return repository.getSet(getSetId())
    }
    val paused: Boolean
    get() = timerSequence.paused
    //fun returns true if button starts or resumes current timer
    fun clickStartBtn(): Boolean{
        if(!timerSequence.started){
            start()
            return true
        }else{
            return if(timerSequence.currAction().exactTimeDefine){
                if(timerSequence.paused){
                    timerSequence.resume()
                    true
                }else{
                    timerSequence.pause()
                    false
                }
            }else{
                timerSequence.next()
                true
            }
        }
    }

    private fun start() {
        timerSequence.start()
    }
    fun stopTimer(){
        timerSequence.stop()
    }
    fun pauseTimer(){
        timerSequence.pause()
    }
    fun resumeTimer(){
        timerSequence.resume()
    }
   /* private fun generateRandomActionSet(){
        viewModelScope.launch {
            val setInfo = SetInfo(0,"set ${Random.nextInt(0, 1000)}")
            val setId = setDao.insertSetInfo(setInfo)
            val actions = List(10){
                Action("action ${Random.nextInt(0,1000)}",Random.nextInt(5,120), Random.nextInt(Int.MIN_VALUE,Int.MAX_VALUE), Random.nextBoolean(),0,setId.toInt())
            }.forEach {
                repository.insertAction(it)
            }
        }
    }*/
}