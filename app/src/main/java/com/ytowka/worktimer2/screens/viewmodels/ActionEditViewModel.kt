package com.ytowka.worktimer2.screens.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.utils.C.Companion.observeOnce
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActionEditViewModel @Inject constructor(private val repository: Repository): ViewModel(){

    private var actionId: Int = -1
    lateinit var action: Action

    fun setup(actionId: Int): LiveData<Action>{
        this.actionId = actionId
        val actionLiveData = repository.getAction(actionId)
        actionLiveData.observeOnce {
            this.action = it
        }
        return actionLiveData
    }
}