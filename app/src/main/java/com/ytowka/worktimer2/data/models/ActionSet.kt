package com.ytowka.worktimer2.data.models

import androidx.room.Embedded
import androidx.room.Relation

class ActionSet(
    @Embedded val setInfo: SetInfo,
    @Relation(parentColumn = "setId", entityColumn = "setId")
    val actions: List<Action>
){

    fun getTotalDuration() = actions.sumOf {
        it.duration
    }

    fun getStringDuration(): String{
        val duration = getTotalDuration()
        val minutes = duration / 60
        val seconds = duration % 60
        return "${minutes}:${seconds}"
    }
}