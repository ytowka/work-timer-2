package com.ytowka.worktimer2.utils.timers.rx

import com.ytowka.worktimer2.utils.timers.Timer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis


//TODO: rx timer implementation
open class RxTimer(override val duration: Long, override val reverseCountDown: Boolean = true, timeUnit: TimeUnit = TimeUnit.SECONDS) : Timer{

    companion object {
        const val msStep = 10L
    }

    private val msDuration = timeUnit.toMillis(duration)

    private var started = false
    private var paused = false
    private var msPassed = 0L
    private var delta = msStep
    override var onFinished = {}

    private var timerObservable = Observable.interval(0, msStep,TimeUnit.MILLISECONDS)
    private lateinit var subscriber: Disposable

    override fun isStarted() = started
    override fun isPaused() = paused
    override fun msPassed() = msPassed

    override fun start(onFinished: () -> Unit) {
        this.onFinished = onFinished
        subscriber = timerObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {

            },{

            },{
               this.onFinished()
            }
            )
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
        //timerJob?.cancel()
        msPassed = 0
        if(!isSkipped) onFinished()
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