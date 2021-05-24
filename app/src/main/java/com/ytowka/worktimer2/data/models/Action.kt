package com.ytowka.worktimer2.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actions_table")
data class Action(
    val name: String,
    val duration: Int,
    val color: Int,
    val exactTimeDefine: Boolean,
    @PrimaryKey(autoGenerate = true) val actionId: Int = 0,
    val setId: Int = 0
)
{
    fun getStringDuration(): String{
        val minutes = duration / 60
        val seconds = duration % 60
        return "${if (minutes<10) "0${minutes}" else minutes}:${if (seconds<10) "0${seconds}" else seconds}"
    }

    fun clone(name: String = this.name,
              duration: Int = this.duration,
              color: Int = this.color,
              exactTimeDefine: Boolean = this.exactTimeDefine,
    ): Action = Action(name, duration, color, exactTimeDefine, this.setId)

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
enum class VocalizeSteps(stepSeconds: Int){
    NOT_VOCALIZE(0),
    EVERY_5(5),
    EVERY_10(10),
    EVERY_15(15),
    EVERY_30(30),
    EVERY_60(60),
    EVERY_3M(180)
}