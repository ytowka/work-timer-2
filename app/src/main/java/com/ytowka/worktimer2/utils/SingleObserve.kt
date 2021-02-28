package com.ytowka.worktimer2.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T) {
            observer(t)
            removeObserver(this)
        }
    })
}