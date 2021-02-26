package com.ytowka.worktimer2.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ytowka.worktimer2.data.models.Action

@Dao
interface ActionDao {

    @Query("SELECT * FROM actions_table WHERE setId = :setId")
    fun getSetActionsAsLiveData(setId: Int): LiveData<List<Action>>

    @Query("SELECT * FROM actions_table WHERE setId = :setId")
    suspend fun getSetActions(setId: Int): List<Action>

    @Insert
    suspend fun insertAction(action: Action)

    @Delete
    suspend fun deleteAction(action: Action)

    @Update
    suspend fun updateAction(action: Action)
}