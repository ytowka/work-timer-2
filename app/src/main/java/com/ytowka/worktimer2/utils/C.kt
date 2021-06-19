package com.ytowka.worktimer2.utils

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.SetInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class C {
    companion object{
        const val DB_NAME = "sets_database"
        const val TAG = "debug"

        private const val appTag = "com.ytowka.worktimer2."

        const val EXTRA_SET_ID = "${appTag}TIMER_SERVICE_SET_ID"
        const val ACTION_INIT_TIMER = "${appTag}TIMER_SERVICE_INIT_TIMER"
        const val ACTION_CHECK_IS_LAUNCHED = "${appTag}TIMER_SERVICE_CHECK_IS_TIMER_LAUNCHED"
        const val ACTION_SHOW_TIMER_FRAGMENT = "${appTag}SHOW_TIMER_FRAGMENT"

        const val NOTIFICATION_STATE_CHANNEL_ID = "${appTag}timer_state_channel_id"
        const val NOTIFICATION_STATE_CHANNEL_NAME = "timerstate channel name"
        const val NOTIFICATION_STATE_ID = 1

        const val NOTIFICATION_ACTION_CHANNEL_ID = "${appTag}timer_action_channel_id"
        const val NOTIFICATION_ACTION_CHANNEL_NAME = "action finished channel name"
        const val NOTIFICATION_ACTION_ID = 2


        fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
            observeForever(object : Observer<T> {
                override fun onChanged(t: T) {
                    observer(t)
                    removeObserver(this)
                }
            })
        }
        fun generateRandomActionSet(coroutineScope: CoroutineScope, repository: Repository){
            coroutineScope.launch {
                val setInfo = SetInfo(0,"set ${Random.nextInt(0, 1000)}")
                val setId = repository.insertSetInfo(setInfo)
                List(10){
                    Action("action ${Random.nextInt(0,1000)}",Random.nextInt(5,120).toLong(), Random.nextInt(Int.MIN_VALUE,Int.MAX_VALUE), Random.nextBoolean(),0,setId)
                }.forEach {
                    repository.insertAction(it)
                }
            }
        }
        fun isColorLight(color: Int, lightThreshold: Int = 96): Boolean{
            return let {
                val r = Color.red(color)
                val g = Color.green(color)
                val b = Color.blue(color)
                (r + g + b) / 3 >= lightThreshold
            }
        }

        fun Long.toStringTime(): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(minutes)
            var string = ""
            if (minutes != 0L) {
                string += "$minutes:"
                if (seconds < 10) string += 0
            }
            string += seconds
            return string
        }

        fun getStringDuration(time: Long): String{
            val minutes = time / 60
            val seconds = time % 60
            return "${if (minutes<10) "0${minutes}" else minutes}:${if (seconds<10) "0${seconds}" else seconds}"
        }
    }
}