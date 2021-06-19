package com.ytowka.worktimer2.screens.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.adapters.EditActionListAdapter
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.data.models.ActionType
import com.ytowka.worktimer2.data.models.ActionType.Companion.applyType
import com.ytowka.worktimer2.data.models.SetInfo
import com.ytowka.worktimer2.utils.C.Companion.observeOnce
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class EditSetViewModel @Inject constructor(private val repository: Repository, @ApplicationContext val context: Context) : ViewModel(), EditActionListAdapter.ActionEditCallback{
    private var setId = -1L

    @Inject
    lateinit var defaultTypes: List<ActionType>

    private lateinit var actions: MutableList<Action>
    private lateinit var setInfo: SetInfo

    private val _actionsLiveData = MutableLiveData<List<Action>>()
    val actionsLiveData: LiveData<List<Action>> = _actionsLiveData

    var isChanged = false
    private set


    fun initViewModel(setId: Long): LiveData<ActionSet>{
        val liveData = MutableLiveData<ActionSet>()

        this.setId = setId
        if(setId == -1L){
            viewModelScope.launch(Dispatchers.Default) {
                actions = mutableListOf()
                setInfo = SetInfo(0,"")
                liveData.postValue(ActionSet(setInfo, actions))
            }
        }else{
            repository.getSet(setId).observeOnce {
                actions = it.actions.toMutableList()
                setInfo = it.setInfo
                liveData.value = it
            }
        }
        return liveData
    }
    fun commitChanges(): Boolean{
        return if(actions.isEmpty()){
            true
        }else{
            if(isChanged){
                GlobalScope.launch {
                    for (i in actions){
                        i.actionId = 0
                        Log.i("debug",i.toString())
                    }
                    repository.deleteActions(setInfo.setId)
                    repository.insertActionSet(ActionSet(setInfo, actions))
                }
                return false
            }
            true
        }
    }
    fun updateSetName(name: String){
        setInfo.name = name
        isChanged = true
    }
    override fun onNameEdit(action: Action, newName: String) {
        action.name = newName
        isChanged = true
    }
    override fun onSecondsEdit(action: Action, seconds: String) {
        val newDuration: Long = action.duration - action.duration%60 + seconds.toLong()
        action.duration = newDuration
        isChanged = true
    }
    override fun onMinutesEdit(action: Action, minutes: String) {
        val seconds = action.duration%60
        action.duration = minutes.toLong()*60+seconds
        isChanged = true
    }

    fun newAction(index: Int){
        val action = Action("",60,0,true,0,setInfo.setId.toLong()).apply {
            applyType(defaultTypes[1])
            name = context.getString(R.string.action)+" $index"
        }
        actions.add(action)
        _actionsLiveData.value = actions
        isChanged = true
    }
    override fun onMove(actions: List<Action>) {
        this.actions = actions.toMutableList()
        isChanged = true
    }
    override fun onDelete(action: Action) {
        GlobalScope.launch {
            repository.deleteAction(action)
        }
        actions.remove(action)
        _actionsLiveData.value = actions
        isChanged = true
    }
}