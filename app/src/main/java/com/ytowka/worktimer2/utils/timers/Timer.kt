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

    fun addCallback(step: Long, callback: (timeState: Long) -> Unit)
    fun clearCallBacks()
}