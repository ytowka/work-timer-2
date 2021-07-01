package com.ytowka.worktimer2.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ytowka.worktimer2.screens.timer.TimerService
import com.ytowka.worktimer2.utils.C
import com.ytowka.worktimer2.utils.C.Companion.observeOnce
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel(){

    var service: TimerService? = null

    var isTimerInitedLiveData = MutableLiveData<Boolean>()

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val serviceBinder = p1 as TimerService.MyBinder
            service = serviceBinder.service
            service!!.isLaunchedLiveData.observeOnce {
                isTimerInitedLiveData.value = it
            }
        }
        override fun onServiceDisconnected(p0: ComponentName?) {
            service = null
        }
    }
}