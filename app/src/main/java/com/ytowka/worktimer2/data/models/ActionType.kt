package com.ytowka.worktimer2.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_action_types")
data class ActionType(
    val name: String,
    val color: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) {
    companion object{
        fun Action.applyType(type: ActionType){
            name = type.name
            color = type.color
        }
    }
}
