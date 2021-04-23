package com.ytowka.worktimer2.screens

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.Action
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
            val setInfo = SetInfo(name = "name: ${Random.nextInt(10,10_000)}")
            val setId = repository.insertSetInfo(setInfo)
            for (i in 0..Random.nextInt(10,15)){
                val r = Random.nextInt(1,254)
                val g = Random.nextInt(1,254)
                val b = Random.nextInt(1,254)

                val color = Color.rgb(r,g,b)
                Log.i("debug","color: $r $g $b")

                val action = Action(name = "action ${Random.nextInt(0,1000)}",duration = Random.nextInt(5,100),color = color,exactTimeDefine = Random.nextBoolean(),setId = setId.toInt())
                repository.insertAction(action)
                Log.i("debug","also added action to ${setId}")
            }
        }
    }
}