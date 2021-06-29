package com.ytowka.worktimer2.utils.timers

import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.utils.timers.coroutine.CoroutineActionTimer

class ActionsTimerSequence(val actionSet: ActionSet) {

    var onActionFinished: (Action) -> Unit = { _ -> }
    var onSequenceFinish = {}

    var paused = false
        private set

    var started = false
        private set

    var restarted = false
        private set

    private var currActionIndex = 0
    fun currentAction() = actionSet.actions[currActionIndex]
    fun currentTimer() = timers[currActionIndex]

    private val timers: List<ActionTimer>

    init {
        timers = List(actionSet.actions.size) { index ->
            CoroutineActionTimer(actionSet.actions[index])
        }
        timers.forEachIndexed { id, timer ->
            if (id == timers.lastIndex) {
                timer.onFinished = {
                    currActionIndex = 0
                    onActionFinished(actionSet.actions[0])
                    currentTimer().setCallbacks(callbackList)

                    started = false
                    restarted = true
                    onSequenceFinish()
                }
            } else {
                timer.onFinished = {
                    currActionIndex = id + 1
                    onActionFinished(currentAction())
                    currentTimer().setCallbacks(callbackList)

                    currentTimer().start()
                }
            }
        }
    }
    private val callbackList = mutableListOf<TimerCallback>()

    fun addTimerCallback(timerCallback: TimerCallback){
        callbackList.add(timerCallback)
        currentTimer().addCallback(timerCallback)
    }
    fun clearTimerCallback(){
        callbackList.clear()
        currentTimer().clearCallBacks()
    }

    fun start() {
        onActionFinished(currentAction())
        restarted = false
        timers.first().start()
        started = true
    }

    fun pause() {
        paused = true
        timers[currActionIndex].pause()
    }

    fun jumpToAction(action: Action) {
        val id = actionSet.actions.indexOf(action)

        timers[currActionIndex].stop(true, true)
        currActionIndex = id
        currentTimer().start()
        currentTimer().setCallbacks(callbackList)
        onActionFinished(currentAction())
    }

    fun jumpToAction(actionPos: Int) {
        timers[currActionIndex].stop(true, true)
        currActionIndex = actionPos
        onActionFinished(currentAction())
        timers[actionPos].start()
    }

    fun resume() {
        paused = false
        timers[currActionIndex].resume()
    }

    fun next() {
        timers[currActionIndex].stop(false, true)
    }

    fun stop() {
        started = false
        timers[currActionIndex].stop(true)
    }

}