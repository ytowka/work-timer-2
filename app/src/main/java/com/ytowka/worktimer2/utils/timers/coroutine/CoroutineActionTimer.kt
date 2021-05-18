package com.ytowka.worktimer2.utils.timers.coroutine

import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.utils.timers.ActionTimer

class CoroutineActionTimer(override val action: Action) : CoroutineTimer(action.duration.toLong(), action.exactTimeDefine), ActionTimer