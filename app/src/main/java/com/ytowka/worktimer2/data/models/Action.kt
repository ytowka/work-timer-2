package com.ytowka.worktimer2.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actions_table")
data class Action(
    @PrimaryKey(autoGenerate = true) val actionId: Int = 0,
    val setId: Int,
    var name: String,
    var duration: Int,
    var color: Int,
    var exactTimeDefine: Boolean
)
{
    fun getStringDuration(): String{
        val minutes = duration / 60
        val seconds = duration % 60
        return "${if (minutes<10) "0${minutes}" else minutes}:${if (seconds<10) "0${seconds}" else seconds}"
    }
}