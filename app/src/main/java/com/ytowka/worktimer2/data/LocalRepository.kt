package com.ytowka.worktimer2.data

import android.content.Context
import com.ytowka.worktimer2.data.database.ActionDao
import com.ytowka.worktimer2.data.database.SetDao
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.data.models.SetInfo

class LocalRepository(context: Context,val setDao: SetDao, val actionDao: ActionDao) : Repository{

    override suspend fun getSets(): List<ActionSet> {
       return setDao.getSets()
    }
    override suspend fun deleteSet(actionSet: ActionSet) {
        setDao.deleteSet(actionSet)
    }


    override suspend fun getActions(setId: Int): List<Action> {
        return actionDao.getSetActions(setId)
    }
    override suspend fun insertAction(action: Action) {
        actionDao.insertAction(action)
    }
    override suspend fun updateAction(action: Action) {
        actionDao.updateAction(action)
    }
    override suspend fun deleteAction(action: Action) {
        actionDao.deleteAction(action)
    }

    override suspend fun insertSetInfo(setInfo: SetInfo) {
        setDao.addSetInfo(setInfo)
    }
    override suspend fun updateSetInfo(setInfo: SetInfo) {
        setDao.updateSetInfo(setInfo)
    }

}