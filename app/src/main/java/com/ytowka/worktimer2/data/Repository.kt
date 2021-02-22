package com.ytowka.worktimer2.data

import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.data.models.SetInfo

interface Repository {
    suspend fun getSets(): List<ActionSet>
    suspend fun deleteSet(actionSet: ActionSet)

    suspend fun getActions(setId: Int): List<Action>
    suspend fun insertAction(action: Action)
    suspend fun updateAction(action: Action)
    suspend fun deleteAction(action: Action)

    suspend fun insertSetInfo(setInfo: SetInfo)
    suspend fun updateSetInfo(setInfo: SetInfo)
}