package com.ytowka.worktimer2.utils.timers.rx

import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.utils.timers.ActionTimer

class RxActionTimer(override val action: Action) : RxTimer(action.duration.toLong(), action.exactTimeDefine), ActionTimer