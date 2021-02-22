package com.ytowka.worktimer2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.SetInfo


@Database(entities = [SetInfo::class, Action::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase(){
    abstract fun getSetDao(): SetDao
    abstract fun getActionDao(): ActionDao
}