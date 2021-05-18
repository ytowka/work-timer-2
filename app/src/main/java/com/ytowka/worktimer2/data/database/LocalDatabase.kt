package com.ytowka.worktimer2.data.database

import android.util.Log
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.RoomDatabase
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionType
import com.ytowka.worktimer2.data.models.SetInfo


@Database(entities = [SetInfo::class, Action::class, ActionType::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase(){
    abstract fun getSetDao(): SetDao
    abstract fun getActionDao(): ActionDao
    abstract fun getActionTypesDao(): ActionTypesDao
}