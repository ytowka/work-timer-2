package com.ytowka.worktimer2.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_action_types")
data class ActionType(
    val name: String,
    val color: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) {
    fun Action.applyType(type: ActionType): Action{
        return this.clone(this@ActionType.name, color =  this@ActionType.color)
    }
}