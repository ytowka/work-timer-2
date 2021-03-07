package com.ytowka.worktimer2.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.utils.observeOnce
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetPreviewViewModel @Inject constructor(private val repository: Repository): ViewModel(){
    private var setId: Int? = null
    private lateinit var actionSet: ActionSet

    fun setup(setId: Int){
        this.setId = setId
        getSet().observeOnce{
           actionSet = it
        }
    }

    var started = false
    private set

    private fun getSetId(): Int{
        if (setId != null) return setId!!
        else throw Exception("setId must be initialized")
    }

    fun getSet(): LiveData<ActionSet>{
        return repository.getSet(getSetId())
    }
    fun getSetActions(){

    }
    fun start(){
        started = true
    }
    fun pause(){

    }
    fun stop(){
        started = false
    }
}