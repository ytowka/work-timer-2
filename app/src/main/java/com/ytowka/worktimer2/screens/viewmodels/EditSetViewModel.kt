package com.ytowka.worktimer2.screens.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class EditSetViewModel @Inject constructor(private val repository: Repository) : ViewModel(){

    private var setId: Int = -1
    set(value) {
        field = value
        if(value == -1){
            editingSet
        }
    }

    private lateinit var editingSet: ActionSet

    private val _editingSetLiveData = MutableLiveData<ActionSet>()

    fun initViewModel(setId: Int): LiveData<ActionSet>{
        this.setId = setId
        return repository.getSet(setId)
    }

    fun addAction(action: Action){
        viewModelScope.launch {
            repository.insertAction(action)
        }
    }
    fun updateAction(action: Action){
        viewModelScope.launch {
            repository.updateAction(action)
        }
    }
    fun deleteCreatedSet(){

    }
    fun updateName(name: String){
        viewModelScope.launch {
            val newSetInfo = repository.getSetInfo(setId).clone(name)
            repository.updateSetInfo(newSetInfo)
        }
    }
}