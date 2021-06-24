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

    @Query("SELECT * FROM actions_table WHERE actionId = :actionId")
    fun getActionAsLiveData(actionId: Long): LiveData<Action>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: Action): Long

    @Delete
    suspend fun deleteAction(action: Action)

    @Query("DELETE FROM actions_table WHERE setId = :setId")
    suspend fun deleteSetActions(setId: Long)

    @Update
    suspend fun updateAction(action: Action)
}