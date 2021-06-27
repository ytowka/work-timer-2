package com.ytowka.worktimer2.utils.timers

import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.utils.timers.coroutine.CoroutineActionTimer
import kotlin.reflect.KClass

class ActionsTimerSequence(val actionSet: ActionSet) {
    var onActionFinished: (ActionTimer) -> Unit = { _ -> }
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

    private val timers: List<ActionTimer>

    init {
        timers = List(actionSet.actions.size) { index ->
            CoroutineActionTimer(actionSet.actions[index])
        }

        timers.forEachIndexed { id, timer ->
            timer.onFinished = {
                if (id == timers.lastIndex) {
                    currActionIndex = 0
                    onActionFinished(timers[0])
                    started = false
                    restarted = true
                    onSequenceFinish()
                } else {
                    currActionIndex = id + 1
                    onActionFinished(currentTimer())
                    currentTimer().start()
                }
            }
        }
    }

    fun start() {
        onActionFinished(timers[currActionIndex])
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
        timers[id].start()
        onActionFinished(timers[id])
    }

    fun jumpToAction(actionPos: Int) {
        timers[currActionIndex].stop(true, true)
        currActionIndex = actionPos
        onActionFinished(timers[actionPos])
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