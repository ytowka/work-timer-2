package com.ytowka.worktimer2.utils

import android.util.Log
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet

class ActionsTimerSequence(private val actionSet: ActionSet){
    var onActionFinished: (ActionTimer) -> Unit = {_->}
    var onSequenceFinish = {}

    var paused = false
    private set

    var started = false
    private set

    var restarted = false
    private set

    private var currActionIndex = 0
    fun currAction() = actionSet.actions[currActionIndex]
    fun currentTimer() = timers[currActionIndex]

    private val timers = List(actionSet.actions.size){ index ->
        val timer = ActionTimer(actionSet.actions[index])
        return@List timer
    }
    init {
        timers.forEachIndexed{id,timer ->
            timer.onFinished = {
                if (id == timers.lastIndex){
                    currActionIndex = 0
                    onActionFinished(timers[0])
                    started = false
                    restarted = true
                    onSequenceFinish()
                }else{
                    currActionIndex = id+1
                    onActionFinished(currentTimer())
                    currentTimer().start()
                }
            }
        }
    }
    fun start(){
        onActionFinished(timers[currActionIndex])
        restarted = false
        timers.first().start()
        started = true
    }
    fun pause(){
        paused = true
        timers[currActionIndex].pause()
    }
    fun jumpToAction(action: Action){
        val id = actionSet.actions.indexOf(action)

        timers[currActionIndex].stop(true, true)
        currActionIndex = id
        onActionFinished(timers[id])
        timers[id].start()
    }
    fun resume(){
        paused = false
        timers[currActionIndex].resume()
    }
    fun next(){
        timers[currActionIndex].stop(false,true)
    }
    fun stop(){
        started = false
        timers[currActionIndex].stop(true)
    }
}