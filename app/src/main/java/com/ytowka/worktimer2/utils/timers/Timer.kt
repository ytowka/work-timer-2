package com.ytowka.worktimer2.utils.timers

interface Timer {
    val duration: Long


    val reverseCountDown: Boolean
    fun isStarted(): Boolean
    fun isPaused(): Boolean
    fun msPassed(): Long
    var onFinished: () -> Unit

    fun start(onFinished: () -> Unit = this.onFinished)
    fun stop(isSkipped: Boolean = false, clearCallbacks: Boolean = false)

    fun pause()
    fun resume()

    fun addCallback(timerCallback: TimerCallback)
    fun clearCallBacks()
    fun setCallbacks(callbacks: List<TimerCallback>)
}
//default time unit is milliseconds
class TimerCallback(private val step: Long = DEFAULT_UPDATE_TIME, private val callback: (timeState: Long) -> Unit) {
    companion object{
        const val DEFAULT_UPDATE_TIME = 13L//ms
    }

    var msDelta = 0L

    fun onTimerUpdate(stepMs: Long, timeState: Long) {
        msDelta += stepMs
        if (msDelta >= step) {
            msDelta -= step
            callback(timeState)
        }
    }
}