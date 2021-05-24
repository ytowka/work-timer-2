package com.ytowka.worktimer2.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ytowka.worktimer2.services.TimerService
import com.ytowka.worktimer2.utils.C
import com.ytowka.worktimer2.utils.C.Companion.observeOnce
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel(){

    private var serviceBinder: TimerService.MyBinder? = null
    var service: TimerService? = null

    var isTimerInitedLiveData = MutableLiveData<Boolean>()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.i("debug","service connected to splash viewModel")
            serviceBinder = p1 as TimerService.MyBinder
            service = serviceBinder!!.service
            service!!.isLaunchedLiveData.observeOnce {
                isTimerInitedLiveData.value = it
                Log.i("debug","service disconnected from splash viewModel")
                context.unbindService(this)
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("debug","service disconnected from splash viewModel")
            serviceBinder = null
            service = null
        }
    }
    init {
        val intentService = Intent(context, TimerService::class.java)
        intentService.putExtra(C.EXTRA_SET_ID, -1)
        intentService.action = C.ACTION_CHECK_IS_LAUNCHED
        context.bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE)
    }
}