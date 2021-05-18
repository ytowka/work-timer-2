package com.ytowka.worktimer2.data.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.data.models.SetInfo

@Dao
interface SetDao {
    @Transaction
    @Query("SELECT * FROM sets_table")
    suspend fun getSets(): List<ActionSet>


    @Transaction
    @Query("SELECT * FROM sets_table")
    fun getSetsAsLiveData(): LiveData<List<ActionSet>>

    @Transaction
    @Query("SELECT * FROM sets_table WHERE setId = :id")
    fun getSetAsLiveData(id: Int): LiveData<ActionSet>

    suspend fun deleteSet(actionSet: ActionSet){
        deleteSetInfo(actionSet.setInfo)
        deleteActions(actionSet.setInfo.setId)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetInfo(setInfo: SetInfo): Long

    @Query("DELETE FROM actions_table WHERE setId = :setId")
    suspend fun deleteActions(setId: Int)

    @Delete
    suspend fun deleteSetInfo(setInfo: SetInfo)

    @Update
    suspend fun updateSetInfo(actionSet: SetInfo)

    @Query("SELECT * FROM sets_table WHERE setId = :id")
    suspend fun getSetInfo(id: Int): SetInfo
}