package com.ytowka.worktimer2.services


import android.app.*
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.ytowka.worktimer2.app.MainActivity
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.screens.toStringTime
import com.ytowka.worktimer2.utils.C
import com.ytowka.worktimer2.utils.C.Companion.observeOnce
import com.ytowka.worktimer2.utils.timers.ActionsTimerSequence
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var repository: Repository
    private var binder = MyBinder()

    private var onActionFinishedCallback: (Action) -> Unit = {}
    var isAppOpened = true
    set(value) {
        Log.i("notif_debug","app ${if (value) "opened" else "closed"}")
        field = value
        setOnActionFinishCallback(onActionFinishedCallback, value)
    }

    val timerSequenceLiveData =  MutableLiveData<ActionsTimerSequence>()
    val isLaunchedLiveData = MutableLiveData<Boolean>()

    private var connectedCount = 0

    override fun onBind(intent: Intent?): IBinder {
        connectedCount ++
        Log.i("service_debug","service bound")
        when(intent!!.action){
            C.ACTION_INIT_TIMER ->{
                if (timerSequenceLiveData.value == null) {
                    createForegroundNotification()
                    val setId = intent.extras!![C.EXTRA_SET_ID] as Int
                    repository.getSet(setId).observeOnce {
                        this.timerSequenceLiveData.value = ActionsTimerSequence(it)
                    }
                }
                Log.i("service_debug","bind service create timer")
            }
            C.ACTION_CHECK_IS_LAUNCHED ->{
                Log.i("service_debug","bind service check timer state: ${timerSequenceLiveData.value != null}")
                isLaunchedLiveData.value = timerSequenceLiveData.value != null
                stopSelf()
            }
            else ->{
                Log.i("service_debug","bind service no action")
            }
        }

        return binder
    }
    override fun onCreate() {
        super.onCreate()
        Log.i("service debug","service created")
    }


    private var stateNotification: Notification? = null
    private var actionNotification: Notification? = null


    fun hideNotification(){
        stopForeground(true)
        stopSelf()
    }

    private fun createForegroundNotification() {
        val intent = Intent(this, MainActivity::class.java).also { it ->
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
        val intent = Intent(this, MainActivity::class.java).also { it ->
            it.action = C.ACTION_SHOW_TIMER_FRAGMENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT)

        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, C.NOTIFICATION_STATE_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_timer)
                .setColor(currentAction().color)
                //.setContentTitle(applicationContext.getString(R.string.timer_started)+", "+applicationContext.getString(R.string.current_action)+": "+time)
                .setContentTitle("${currentAction().name}: $time")
                .setContentIntent(pendingIntent)
        if(paused){
            notificationBuilder.setContentText(applicationContext.getString(R.string.pause))
        }
        stateNotification = notificationBuilder.build()
        notificationManager.notify(C.NOTIFICATION_STATE_ID, stateNotification)
    }


    private fun notifyActionFinished(action: Action) {
         val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Log.i("notif_debug","notify action changed")
        val intent = Intent(this, MainActivity::class.java).also { it ->
            it.action = C.ACTION_SHOW_TIMER_FRAGMENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT)

        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, C.NOTIFICATION_ACTION_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(applicationContext.getString(R.string.new_action)+action.name+": ${action.getStringDuration()}")
                .setContentIntent(pendingIntent)
        actionNotification = notificationBuilder.build()
        notificationManager.notify(C.NOTIFICATION_ACTION_ID, actionNotification)
    }

    private lateinit var progressBarUpdate: (Long) -> Unit
    private lateinit var timeTextUpdate: (Long) -> Unit

    private fun setOnActionFinishCallback(callback: (Action) -> Unit, isAppOpened: Boolean){
        timerSequenceLiveData.observeOnce { timerSequence ->
            timerSequence.onActionFinished = {
                if(!isAppOpened){
                    notifyActionFinished(it.action)
                }
                callback(it.action)
                if (it.reverseCountDown) {
                    it.addCallback(13, progressBarUpdate)
                } else {
                    it.addCallback(13) { time ->
                        progressBarUpdate((it.action.duration * 1000).toLong())
                    }
                }
                it.addCallback(1000) { time ->
                    updateStateNotification(time.toStringTime())
                    timeTextUpdate(time)
                }
            }
        }
    }

    fun setOnActionFinishCallback(callback: (Action) -> Unit) {
        setOnActionFinishCallback(callback,true)
    }
    fun setupCallbacks(progressBarUpdate: (Long) -> Unit, timeTextUpdate: (Long) -> Unit) {
        timerSequenceLiveData.observeOnce { timerSequence ->
            this.progressBarUpdate = progressBarUpdate
            this.timeTextUpdate = timeTextUpdate
            timerSequence.currentTimer().clearCallBacks()
            val it = timerSequence.currentTimer()
            if (it.reverseCountDown) {
                it.addCallback(13, progressBarUpdate)
            } else {
                it.addCallback(13) { time ->
                    progressBarUpdate((it.action.duration * 1000).toLong())
                }
            }
            it.addCallback(1000) { time ->
                updateStateNotification(time.toStringTime())
                timeTextUpdate(time)
            }
        }
    }

    /*

    timerSequenceLiveData.observeOnce { timerSequence ->

    }

    */

    fun isStarted() = timerSequenceLiveData.value!!.started
    fun isRestarted() = timerSequenceLiveData.value!!.restarted

    fun currentAction() = timerSequenceLiveData.value!!.currAction()
    fun setOnSequenceFinish(onFinish: () -> Unit) {
        timerSequenceLiveData.observeOnce { timerSequence ->
            timerSequence.onSequenceFinish = onFinish
        }
    }
    val paused: Boolean get() = timerSequenceLiveData.value!!.paused

    fun jumpTo(action: Action) {
        timerSequenceLiveData.observeOnce { timerSequence ->
            timerSequence.resume()
            timerSequence.jumpToAction(action)
        }
    }
    fun getCurrentTimerTime(): Long {
        return timerSequenceLiveData.value!!.currentTimer().msPassed()
    }
    fun startTimers() {
        timerSequenceLiveData.observeOnce { timerSequence ->
            timerSequence.start()
        }
    }
    fun nextTimer() {
        timerSequenceLiveData.observeOnce { timerSequence ->
            timerSequence.next()
        }
    }
     fun isTimerPaused(): Boolean {
        return timerSequenceLiveData.value!!.paused
    }
    fun stopTimer() {
        timerSequenceLiveData.observeOnce { timerSequence ->
            hideNotification()
            timerSequence.stop()
        }
    }
    fun pauseTimer() {
        timerSequenceLiveData.observeOnce { timerSequence ->
            updateStateNotification(timerSequence.currentTimer().msPassed().toStringTime(),true)
            timerSequence.pause()
        }
    }
    fun resumeTimer() {
        timerSequenceLiveData.observeOnce { timerSequence ->
            updateStateNotification(timerSequence.currentTimer().msPassed().toStringTime(),false)
            timerSequence.resume()
        }
    }

    inner class MyBinder : Binder(){
        val service
        get() = this@TimerService
    }
    override fun onDestroy() {
        Log.i("service_debug","destroy service")
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        connectedCount --
        Log.i("service_debug","unbind service")
        return super.onUnbind(intent)
    }
}