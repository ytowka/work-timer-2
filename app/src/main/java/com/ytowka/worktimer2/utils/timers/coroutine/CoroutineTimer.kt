package com.ytowka.worktimer2.utils.timers.coroutine

import com.ytowka.worktimer2.utils.timers.Timer
import com.ytowka.worktimer2.utils.timers.TimerCallback
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

open class CoroutineTimer(
    final override val duration: Long,
    override val reverseCountDown: Boolean = true,
    timeUnit: TimeUnit = TimeUnit.SECONDS
) : Timer {
    companion object {

        // how frequently timer will check callbacks
        const val msStep = 10L
    }

    private val msDuration = timeUnit.toMillis(duration)

    private var started = false
    private var paused = false
    private var msPassed = 0L

    private var timerJob: Job? = null
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
                    this@CoroutineTimer.onFinished()
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

    // `isSkipped` argument equals `true` when we skipped this timer so we don't need to execute onFinish() callback
    override fun stop(isSkipped: Boolean, clearCallbacks: Boolean) {
        if (clearCallbacks) clearCallBacks()
        started = false
        paused = false
        timerJob?.cancel()
        msPassed = 0
        if (!isSkipped) onFinished()
    }

    private var callbackList = mutableListOf<TimerCallback>()

    override fun addCallback(timerCallback: TimerCallback) {
        callbackList.add(timerCallback)
    }
    override fun setCallbacks(callbacks: List<TimerCallback>){
        callbackList = callbacks.toMutableList()
    }
    override fun clearCallBacks() {
        callbackList.clear()
    }
}

