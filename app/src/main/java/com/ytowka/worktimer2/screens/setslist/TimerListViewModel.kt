package com.ytowka.worktimer2.screens.setslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.utils.C
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

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
    fun filterList(query: String): Single<List<ActionSet>>{
        return Single.create {emitter ->
            val lowCaseQuery = query.toLowerCase(Locale.ROOT)
            val filteredList = mutableListOf<ActionSet>()
            getSetList().value?.forEach {
                val lowerCaseText = it.setInfo.name.toLowerCase(Locale.ROOT)
                if (lowerCaseText.contains(lowCaseQuery)) {
                    filteredList.add(it)
                }
            }
            if(!emitter.isDisposed){
                emitter.onSuccess(filteredList)
            }
        }
    }
    fun addSetItem(){
        C.generateRandomActionSet(viewModelScope,repository)
    }
}