package com.ytowka.worktimer2.data

import androidx.lifecycle.LiveData
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.data.models.ActionType
import com.ytowka.worktimer2.data.models.SetInfo

interface Repository {
    fun getSets(): LiveData<List<ActionSet>>
    fun getSet(id: Long): LiveData<ActionSet>
    suspend fun getSetInfo(id: Int): SetInfo
    suspend fun deleteSet(actionSet: ActionSet)

    fun getActions(setId: Int): LiveData<List<Action>>
    fun getAction(actionId: Long): LiveData<Action>
    suspend fun insertAction(action: Action): Long
    suspend fun updateAction(action: Action)
    suspend fun deleteAction(action: Action)
    suspend fun deleteActions(setId: Long)

    fun getActionTypes(): LiveData<List<ActionType>>
    suspend fun insertActionType(actionType: ActionType)
    suspend fun deleteActionType(actionType: ActionType)

    suspend fun insertSetInfo(setInfo: SetInfo): Long
    suspend fun deleteSetInfo(setInfo: SetInfo)
    suspend fun updateSetInfo(setInfo: SetInfo)

    suspend fun insertActionSet(actionSet: ActionSet, insertNew: Boolean): Long
}