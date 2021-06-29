package com.ytowka.worktimer2.screens.timer


import android.app.*
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ytowka.worktimer2.app.MainActivity
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.utils.C
import com.ytowka.worktimer2.utils.C.Companion.observeOnce
import com.ytowka.worktimer2.utils.C.Companion.toStringTime
import com.ytowka.worktimer2.utils.timers.ActionsTimerSequence
import com.ytowka.worktimer2.utils.timers.TimerCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {
    @Inject
    lateinit var repository: Repository
    private var binder = MyBinder()

    var isAppOpened = true
        set(value) {
            field = value
            if (this::timerSequence.isInitialized) {
                setOnActionFinishCallback(onActionFinishedCallback)
            }
        }
    var sound = true
        set(value) {
            field = value
            if (this::timerSequence.isInitialized) {
                Log.i("debug","sound setter: $field")
                setOnActionFinishCallback(onActionFinishedCallback)
            }
        }

    private var onActionFinishedCallback: (Action) -> Unit = {}

    private lateinit var timerSequence: ActionsTimerSequence
    var setId = 0L
        private set

    val timerSequenceLiveData = MutableLiveData<ActionsTimerSequence>()
    val isLaunchedLiveData = MutableLiveData<Boolean>()

    private var stateNotification: Notification? = null
    private var actionNotification: Notification? = null

    private var actionSetLiveData: LiveData<ActionSet>? = null

    fun updateSet() {
        if(!timerSequence.started){
            actionSetLiveData = repository.getSet(setId)
            actionSetLiveData!!.observeOnce {
                Log.i("service_debug", "new timer sequence")
                timerSequence.stop()
                timerSequence = ActionsTimerSequence(it)
                timerSequenceLiveData.value = timerSequence
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.i("service_debug", "service bound: $intent")
        when (intent!!.action) {
            C.ACTION_INIT_TIMER -> {
                val setId = intent.extras!![C.EXTRA_SET_ID] as Long
                if (!isTimerCreated) {
                    this.setId = setId
                    createForegroundNotification()
                    Log.i("service_debug", "service bound to init timer")
                    actionSetLiveData = repository.getSet(setId)
                    actionSetLiveData!!.observeOnce {
                        Log.i("service_debug", "new timer sequence")
                        this.timerSequence = ActionsTimerSequence(it)
                        this.timerSequenceLiveData.value = timerSequence
                    }
                    isTimerCreated = true
                }
            }
            C.ACTION_CHECK_IS_LAUNCHED -> {
                Log.i("service_debug", "service bound to check state $isTimerCreated")
                isLaunchedLiveData.value = isTimerCreated
            }
        }

        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("life_service_debug", "service created: $this")
    }

    private var isTimerCreated = false


    private fun createForegroundNotification() {
        val intent = Intent(this, MainActivity::class.java).also {
            it.action = C.ACTION_SHOW_TIMER_FRAGMENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT)

        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, C.NOTIFICATION_STATE_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(applicationContext.getString(R.string.timer_started))
                .setContentIntent(pendingIntent)
        stateNotification = notificationBuilder.build()

        startForeground(C.NOTIFICATION_STATE_ID, stateNotification)
    }

    private fun updateStateNotification(time: String, paused: Boolean = false) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java).also {
            it.action = C.ACTION_SHOW_TIMER_FRAGMENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT)

        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, C.NOTIFICATION_STATE_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_timer)
                .setColor(currentAction().color)
                .setContentTitle("${currentAction().name}: $time")
                .setContentIntent(pendingIntent)
        if (paused) {
            notificationBuilder.setContentText(applicationContext.getString(R.string.pause))
        }
        stateNotification = notificationBuilder.build()
        notificationManager.notify(C.NOTIFICATION_STATE_ID, stateNotification)
    }


    private fun notifyActionFinished(action: Action) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java).also { it ->
            it.action = C.ACTION_SHOW_TIMER_FRAGMENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT)

        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, C.NOTIFICATION_ACTION_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(applicationContext.getString(R.string.new_action) + action.name + ": ${action.getStringDuration()}")
                .setContentIntent(pendingIntent)
        actionNotification = notificationBuilder.build()
        notificationManager.notify(C.NOTIFICATION_ACTION_ID, actionNotification)
    }

    private lateinit var progressBarUpdate: (Long) -> Unit
    private lateinit var timeTextUpdate: (Long) -> Unit



    fun setOnActionFinishCallback(callback: (Action) -> Unit) {
        Log.i("debug","sound fun: $sound")
        this.onActionFinishedCallback = callback
        timerSequence.onActionFinished = {
            if (!isAppOpened && sound) {
                notifyActionFinished(it)
            }
            callback(it)
        }
    }
    fun setupCallbacks(progressBarUpdate: (Long) -> Unit, timeTextUpdate: (Long) -> Unit) {
        this.progressBarUpdate = progressBarUpdate
        this.timeTextUpdate = timeTextUpdate

        timerSequence.clearTimerCallback()
        timerSequence.addTimerCallback(TimerCallback(13L, progressBarUpdate))
        timerSequence.addTimerCallback(TimerCallback(1000L) {
            updateStateNotification(it.toStringTime())
            timeTextUpdate(it)
        })
    }

    fun isStarted(): Boolean {
        return timerSequence.started
    }

    fun isRestarted() = timerSequence.restarted

    fun currentAction() = timerSequence.currentAction()

    fun setOnSequenceFinish(onFinish: () -> Unit) {
        timerSequence.onSequenceFinish = onFinish
    }

    val paused: Boolean get() = timerSequence.paused

    fun jumpTo(action: Action) {
        timerSequence.resume()
        timerSequence.jumpToAction(action)

    }

    fun getCurrentTimerTime(): Long {
        return timerSequence.currentTimer().msPassed()
    }

    fun startTimers() {
        timerSequence.start()
    }

    fun nextTimer() {
        timerSequence.next()

    }

    fun isTimerPaused(): Boolean {
        return timerSequence.paused
    }

    fun stopTimer() {
        if (isTimerCreated) {
            timerSequence.stop()
            stopForeground(true)
            stopSelf()
        }
    }

    fun pauseTimer() {
        updateStateNotification(timerSequence.currentTimer().msPassed().toStringTime(), true)
        timerSequence.pause()
    }

    fun resumeTimer() {
        timerSequence.resume()
        updateStateNotification(timerSequence.currentTimer().msPassed().toStringTime(), false)
    }

    inner class MyBinder : Binder() {
        val service
            get() = this@TimerService
    }
}