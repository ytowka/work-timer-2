package com.ytowka.worktimer2.utils.timers.coroutine

import com.ytowka.worktimer2.utils.timers.Timer
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

open class CoroutineTimer(
    final override val duration: Long,
    override val reverseCountDown: Boolean = true,
    timeUnit: TimeUnit = TimeUnit.SECONDS
) : Timer {
    companion object {
        const val msStep = 10L
    }

    private val msDuration = timeUnit.toMillis(duration)

    private var started = false
    private var timerJob: Job? = null
    private var paused = false
    private var msPassed = 0L
    private var delta = msStep
    override var onFinished = {}

    override fun isStarted() = started
    override fun isPaused() = paused
    override fun msPassed() = msPassed

    override fun start(onFinished: () -> Unit) {
        this.onFinished = onFinished
        timerJob = GlobalScope.launch {
            try {
                started = true
                if (reverseCountDown) {
                    msPassed = msDuration
                    while (msPassed >= 0 && isActive) {
                        val startTime = System.currentTimeMillis()
                        if (!paused) {
                            delay(msStep)
                            withContext(Dispatchers.Main) {
                                callbackList.forEach {
                                    it.onTimerUpdate(delta, msPassed)
                                }
                            }
                            msPassed -= delta
                        }
                        delta = System.currentTimeMillis() - startTime
                    }
                } else {
                    while (isActive) {
                        val startTime = System.currentTimeMillis()
                        if (!paused) {
                            delay(msStep)
                            msPassed += delta
                            withContext(Dispatchers.Main) {
                                callbackList.forEach {
                                    it.onTimerUpdate(delta, msPassed)
                                }
                            }
                        }
                        delta = System.currentTimeMillis() - startTime
                    }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    onFinished()
                }
                stop(true, true)
            }
        }
    }

    override fun pause() {
        paused = true
    }

    override fun resume() {
        paused = false
    }

    override fun stop(isSkipped: Boolean, clearCallbacks: Boolean) {
        if (clearCallbacks) clearCallBacks()
        started = false
        paused = false
        timerJob?.cancel()
        msPassed = 0
        if (!isSkipped) onFinished()
    }

    private val callbackList = mutableListOf<TimerCallback>()

    override fun addCallback(step: Long, callback: (timeState: Long) -> Unit) {
        callbackList.add(TimerCallback(step, callback))
    }

    override fun clearCallBacks() {
        callbackList.clear()
    }
}

//default time unit is milliseconds
class TimerCallback(private val step: Long, private val callback: (timeState: Long) -> Unit) {
    var msDelta = 0L

    fun onTimerUpdate(stepMs: Long, timeState: Long) {
        msDelta += stepMs
        if (msDelta >= step) {
            msDelta -= step
            callback(timeState)
        }
    }
}
