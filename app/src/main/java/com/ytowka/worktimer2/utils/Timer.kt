package com.ytowka.worktimer2.utils

import android.app.Notification
import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

open class Timer(duration: Long,val reverseCountDown: Boolean = true, timeUnit: TimeUnit = TimeUnit.SECONDS) {
    companion object {
        const val msStep = 10L
    }

    private val msDuration = timeUnit.toMillis(duration)

    var started = false
        private set

    var timerJob: Job? = null
        private set
    var paused = false
        private set

    var msPassed = 0L
    private set

    private var delta = msStep

    var onFinished = {}


    fun start(onFinished: () -> Unit = this.onFinished) {
        this.onFinished = onFinished
        timerJob = GlobalScope.launch {
            try {
                started = true
                if (reverseCountDown) {
                    msPassed = msDuration

                    while (msPassed >= 0 && isActive) {
                        delta = measureTimeMillis {
                            if (!paused) {
                                delay(msStep)
                                withContext(Dispatchers.Main){
                                    callbackList.forEach {
                                        it.onTimerUpdate(delta, msPassed)
                                    }
                                }
                                msPassed -= delta
                            }
                        }
                    }
                } else {
                    while (isActive) {
                        delta = measureTimeMillis {
                            if (!paused) {
                                delay(msStep)
                                withContext(Dispatchers.Main){
                                    callbackList.forEach {
                                        it.onTimerUpdate(delta, msPassed)
                                    }
                                }
                                msPassed += delta
                            }
                        }
                    }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    onFinished()
                }
                stop(true,true)
            }
        }
    }

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }

    fun stop(isSkipped: Boolean = false, clearCallbacks: Boolean = false) {
        if (clearCallbacks) clearCallBacks()
        started = false
        paused = false
        timerJob?.cancel()
        msPassed = 0
        if(!isSkipped) onFinished()
    }

    private val callbackList = mutableListOf<TimerCallback>()

    fun addCallback(step: Long, callback: (timeState: Long) -> Unit) {
        callbackList.add(TimerCallback(step, callback))
    }

    fun clearCallBacks() {
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
