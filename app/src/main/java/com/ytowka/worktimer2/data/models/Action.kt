package com.ytowka.worktimer2.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actions_table")
data class Action(
    var name: String,
    var duration: Int,
    var color: Int,
    var exactTimeDefine: Boolean,
    @PrimaryKey(autoGenerate = true) val actionId: Int = 0,
    val setId: Int = 0

)
{
    fun getStringDuration(): String{
        val minutes = duration / 60
        val seconds = duration % 60
        return "${if (minutes<10) "0${minutes}" else minutes}:${if (seconds<10) "0${seconds}" else seconds}"
    }

    override fun equals(other: Any?): Boolean {
        if(other is Action){
            if(other.actionId == actionId) return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + duration
        result = 31 * result + color
        result = 31 * result + exactTimeDefine.hashCode()
        result = 31 * result + actionId
        result = 31 * result + setId
        return result
    }
}