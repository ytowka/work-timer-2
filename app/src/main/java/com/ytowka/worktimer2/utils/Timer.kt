package com.ytowka.worktimer2.utils

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class Timer(duration: Long,private val reverseCountDown: Boolean,timeUnit: TimeUnit = TimeUnit.SECONDS){
    companion object{
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

    fun start(onFinished: ()->Unit){
        timerJob = GlobalScope.launch {
            started = true
            if (reverseCountDown){
                msPassed = msDuration
                while (msPassed >= 0){
                    if(!paused){
                        delay(msStep)
                        callbackList.forEach {
                            it.onTimerUpdate(msStep,msPassed)
                        }
                        msPassed -= msStep
                    }

                }
                withContext(Dispatchers.Main){
                    onFinished()
                }
                stop()
            }else{
                while (msPassed <= msDuration){
                    if(!paused){
                        delay(msStep)
                        callbackList.forEach {
                            it.onTimerUpdate(msStep, msPassed)
                        }
                        msPassed += msStep
                    }
                }
                withContext(Dispatchers.Main){
                    onFinished()
                }
                stop()
            }
        }
    }
    fun pause(){
        paused = true
    }
    fun resume(){
        paused = false
    }
    fun stop(clearCallbacks: Boolean = false){
        if(clearCallbacks) clearCallBacks()
        started = false
        timerJob?.cancel()
        msPassed = 0
    }

    private val callbackList = mutableListOf<TimerCallback>()
    fun addCallback(timeUnit: TimeUnit,step: Long,callback: (TimeUnit: TimeUnit, timeState: Long)->Unit){
        callbackList.add(TimerCallback(timeUnit,step,callback))
    }

    fun clearCallBacks(){
        callbackList.clear()
    }
    private class TimerCallback(private val timeUnit: TimeUnit,private val step: Long, private val callback: (timeUnit: TimeUnit, timeState: Long) -> Unit){
        var msDelta = 0L

        suspend fun onTimerUpdate(stepMs: Long, timeState: Long){
            msDelta += stepMs
            if(msDelta >= timeUnit.toMillis(step)){
                msDelta -= timeUnit.toMillis(step)
                withContext(Dispatchers.Main){
                    callback(timeUnit, timeUnit.convert(timeState, TimeUnit.MILLISECONDS))
                }
            }
        }
    }
}
