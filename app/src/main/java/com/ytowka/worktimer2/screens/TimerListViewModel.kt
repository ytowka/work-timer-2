package com.ytowka.worktimer2.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.data.models.SetInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class TimerListViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    var selectingMode = false
    var searchingMode = false


    private var ld: LiveData<List<ActionSet>>? = null

    fun getSetList(): LiveData<List<ActionSet>>{
        if (ld == null){
            this.ld = repository.getSets()
            return this.ld!!
        }else{
            return ld!!
        }
    }
    fun deleteSet(actionSet: ActionSet){
        viewModelScope.launch {
            repository.deleteSet(actionSet)
        }
    }
    fun deleteSets(list: List<ActionSet>){
        list.forEach {
            deleteSet(it)
        }
    }
    fun addSetItem(){
        viewModelScope.launch {
            repository.insertSetInfo(SetInfo(0,"name: ${Random.nextInt(10,10_000)}"))
        }
    }
}