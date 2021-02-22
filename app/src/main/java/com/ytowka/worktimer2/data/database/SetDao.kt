package com.ytowka.worktimer2.data.database

import android.util.Log
import androidx.room.*
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.data.models.SetInfo

@Dao
interface SetDao {
    @Transaction
    @Query("SELECT * FROM sets_table")
    suspend fun getSets(): List<ActionSet>

    suspend fun deleteSet(actionSet: ActionSet){
        deleteSetInfo(actionSet.setInfo)
        deleteActions(actionSet.setInfo.setId)
    }

    @Insert
    suspend fun addSetInfo(setInfo: SetInfo)

    @Query("DELETE FROM actions_table WHERE setId = :setId")
    suspend fun deleteActions(setId: Int)

    @Delete
    suspend fun deleteSetInfo(setInfo: SetInfo)

    @Update
    suspend fun updateSetInfo(actionSet: SetInfo)
}