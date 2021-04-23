package com.ytowka.worktimer2.utils

import android.app.Notification
import com.ytowka.worktimer2.data.models.Action

class ActionTimer(val action: Action) : Timer(action.duration.toLong(), action.exactTimeDefine)