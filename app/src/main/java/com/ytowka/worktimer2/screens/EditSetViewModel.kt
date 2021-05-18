package com.ytowka.worktimer2.screens

import androidx.lifecycle.LiveData
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

    var setId: Int? = null
    private fun getSetId(): Int{
        if (setId != null) return setId!!
        else throw Exception("setId must be initialized")
    }

    fun getSet(): LiveData<ActionSet>{
        return repository.getSet(getSetId())
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
            val newSetInfo = repository.getSetInfo(getSetId())
            newSetInfo.name = name
            repository.updateSetInfo(newSetInfo)
        }
    }
}