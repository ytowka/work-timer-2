package com.ytowka.worktimer2.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class SingleObserve {
    fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
        observeForever(object : Observer<T> {
            override fun onChanged(t: T) {
                observer(t)
                removeObserver(this)
            }
        })
    }
}