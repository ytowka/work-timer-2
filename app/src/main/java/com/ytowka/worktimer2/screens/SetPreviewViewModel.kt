package com.ytowka.worktimer2.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.ActionSet
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SetPreviewViewModel @Inject constructor(private val repository: Repository): ViewModel(){
    var setId: Int? = null
    private fun getSetId(): Int{
        if (setId != null) return setId!!
        else throw Exception("setId must be initialized")
    }
    fun getSet(): LiveData<ActionSet>{
        return repository.getSet(getSetId())
    }
    fun getSetActions(){

    }
}