package com.ytowka.worktimer2.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sets_table")
data class SetInfo (
    @PrimaryKey(autoGenerate = true) val setId: Int = 0,
    var name: String,
)