package com.ytowka.worktimer2.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actions_table")
data class Action(
    var name: String,
    var duration: Long, //seconds
    var color: Int,
    var exactTimeDefine: Boolean,
    @PrimaryKey(autoGenerate = true) var actionId: Int = 0,
    var setId: Long = 0
)
{
    fun getStringDuration(): String{
        val minutes = duration / 60
        val seconds = duration % 60
        return "${if (minutes<10) "0${minutes}" else minutes}:${if (seconds<10) "0${seconds}" else seconds}"
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