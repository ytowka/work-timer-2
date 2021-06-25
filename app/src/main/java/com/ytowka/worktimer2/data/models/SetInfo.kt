package com.ytowka.worktimer2.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sets_table")
data class SetInfo (
    @PrimaryKey(autoGenerate = true) var setId: Long = 0,
    var name: String,
    var lastOpen: Long = System.currentTimeMillis()
)