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
        return "${if (minutes<10) "0${minutes}" else minutes}:${if (seconds<10) "0${seconds}" else seconds}"
    }

    override fun equals(other: Any?): Boolean {
        if(other is ActionSet){
            if(other.setInfo.setId == setInfo.setId) return true
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return setInfo.setId
    }
    fun withFinishAction(action: Action): ActionSet{
        val newList = actions.toMutableList()
        newList.add(action)
        return ActionSet(setInfo,newList)
    }
}