package com.ytowka.worktimer2.utils.timers

import com.ytowka.worktimer2.data.models.Action

interface ActionTimer : Timer{
    val action: Action
}