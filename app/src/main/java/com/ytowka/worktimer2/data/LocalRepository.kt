package com.ytowka.worktimer2.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.ytowka.worktimer2.data.database.ActionDao
import com.ytowka.worktimer2.data.database.ActionTypesDao
import com.ytowka.worktimer2.data.database.SetDao
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.data.models.ActionType
import com.ytowka.worktimer2.data.models.SetInfo

class LocalRepository(val setDao: SetDao, val actionDao: ActionDao, val actionTypesDao: ActionTypesDao) : Repository{

    override fun getSets(): LiveData<List<ActionSet>> {
       return setDao.getSetsAsLiveData()
    }

    override fun getSet(id: Long): LiveData<ActionSet> {
        return setDao.getSetAsLiveData(id)
    }

    override suspend fun getSetInfo(id: Int): SetInfo {
        return setDao.getSetInfo(id)
    }

    override suspend fun deleteSet(actionSet: ActionSet) {
        setDao.deleteSet(actionSet)
    }

    override fun getActions(setId: Int): LiveData<List<Action>> {
        return actionDao.getSetActionsAsLiveData(setId)
    }

    override fun getAction(actionId: Long): LiveData<Action> {
        return actionDao.getActionAsLiveData(actionId)
    }

    override suspend fun insertAction(action: Action) : Long{
        action.actionId = actionDao.insertAction(action)
        return action.actionId
    }
    override suspend fun updateAction(action: Action) {
        actionDao.updateAction(action)
    }
    override suspend fun deleteAction(action: Action) {
        actionDao.deleteAction(action)
    }

    override suspend fun deleteActions(setId: Long) {
        actionDao.deleteSetActions(setId)
    }

    override fun getActionTypes(): LiveData<List<ActionType>> {
        return actionTypesDao.getActionTypes()
    }

    override suspend fun insertActionType(actionType: ActionType) {
        actionTypesDao.addActionType(actionType)
    }

    override suspend fun deleteActionType(actionType: ActionType) {
        actionTypesDao.removeActionType(actionType)
    }

    override suspend fun insertSetInfo(setInfo: SetInfo) : Long{
        return setDao.insertSetInfo(setInfo)
    }

    override suspend fun deleteSetInfo(setInfo: SetInfo) {
        setDao.deleteSetInfo(setInfo)
    }

    override suspend fun updateSetInfo(setInfo: SetInfo) {
        setDao.updateSetInfo(setInfo)
    }

    override suspend fun insertActionSet(actionSet: ActionSet): Long {
        val setInfoId = setDao.insertSetInfo(actionSet.setInfo)
        actionSet.setInfo.setId = setInfoId

        actionSet.actions.forEach {
            it.setId = setInfoId
            it.actionId = actionDao.insertAction(it)
        }
        return setInfoId
    }
}