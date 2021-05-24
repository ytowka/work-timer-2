package com.ytowka.worktimer2.data.database

import androidx.room.*
import com.ytowka.worktimer2.data.models.ActionType

@Dao
interface ActionTypesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addActionType(at: ActionType)

    @Delete
    fun removeActionType(at: ActionType)

    @Query("SELECT * FROM saved_action_types")
    suspend fun getActionTypes(): List<ActionType>
}