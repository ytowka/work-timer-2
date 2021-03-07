package com.ytowka.worktimer2.utils

import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet

class ActionsTimerSequence(private val actionSet: ActionSet,var onActionFinished: (nextAction: Action) -> Unit, )