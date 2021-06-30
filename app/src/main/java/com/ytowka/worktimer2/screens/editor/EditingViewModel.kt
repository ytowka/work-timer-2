package com.ytowka.worktimer2.screens.editor

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.adapters.EditActionListAdapter
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionSet
import com.ytowka.worktimer2.data.models.ActionType
import com.ytowka.worktimer2.data.models.ActionType.Companion.applyType
import com.ytowka.worktimer2.data.models.SetInfo
import com.ytowka.worktimer2.utils.C
import com.ytowka.worktimer2.utils.C.Companion.observeOnce
import com.ytowka.worktimer2.utils.C.Companion.safeToInt
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class EditingViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext val context: Context
) : ViewModel(), EditActionListAdapter.ActionEditCallback {


    // value -2 means viewmodel is not initialized, -1 means viewmodel is creating new a set
    private var setId = -2L
    lateinit var currAction: Action

    @Inject
    lateinit var defaultTypes: List<ActionType>

    private lateinit var actions: MutableList<Action>
    private lateinit var setInfo: SetInfo

    private var _actionsLiveData = MutableLiveData<List<Action>>()
    var actionsLiveData: LiveData<List<Action>> = _actionsLiveData

    private var _infoLiveData = MutableLiveData<SetInfo>()
    var infoLiveData: LiveData<SetInfo> = _infoLiveData

    var isChanged = false
        private set

    val isEmpty: Boolean
        get() {
            return actions.isEmpty()
        }

    private val initializationLiveData = MutableLiveData<Boolean>()


    //if set is new it will return `true`
    fun initViewModel(setId: Long): LiveData<Boolean> {
        if (this.setId == -2L) {
            this.setId = setId
            if (setId == -1L) {
                actions = mutableListOf()
                setInfo = SetInfo(0, "")

                updateActions()
                _infoLiveData.value = setInfo

                initializationLiveData.value = true

            } else {
                repository.getSet(setId).observeOnce {
                    actions = it.actions.toMutableList()
                    setInfo = it.setInfo

                    updateActions()
                    _infoLiveData.value = setInfo

                    initializationLiveData.value = false
                }
            }
        }
        return initializationLiveData
    }

    //returns true if set is empty
    fun commitChanges(): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        if (actions.isEmpty()) {
            Log.i("debug","actions is empty")
            GlobalScope.launch {
                repository.deleteActions(setInfo.setId)
                repository.deleteSetInfo(setInfo)

                liveData.postValue(true)
            }
        } else {
            if (isChanged) {
                GlobalScope.launch {
                    Log.i("debug","actions is not empty")
                    repository.deleteActions(setInfo.setId)

                    repository.insertActionSet(ActionSet(setInfo, actions), false)
                    liveData.postValue(false)
                    withContext(Dispatchers.Main) {
                        updateActions()
                    }
                }
            }
        }
        return liveData
    }
    fun deleteSet(): LiveData<Boolean>{
        val liveData = MutableLiveData<Boolean>()
        GlobalScope.launch {
            repository.deleteActions(setInfo.setId)
            repository.deleteSetInfo(setInfo)
            liveData.postValue(true)
        }
        return liveData
    }

    // --------------------------------list editor methods----------------------------------------------------------------------

    fun updateSetName(name: String) {
        setInfo.name = name
        isChanged = true
    }

    override fun onNameEdit(action: Action, newName: String) {
        action.name = newName
        isChanged = true
    }

    override fun onSecondsEdit(action: Action, seconds: String) {
        secondsEdit(seconds, action)
    }

    override fun onMinutesEdit(action: Action, minutes: String) {
        minutesEdit(minutes, action)
    }

    override fun onMove(posFrom: Int, posTo: Int): List<Action> {
        val buffer = actions[posFrom]
        actions.removeAt(posFrom)
        actions.add(posTo, buffer)
        //updateActions()
        isChanged = true
        return actions
    }

    override fun onDelete(pos: Int) {
        actions.removeAt(pos)
        updateActions()
        isChanged = true
    }

    fun secondsEdit(seconds: String, action: Action = currAction) {
        val newDuration: Long = action.duration - action.duration % 60 + seconds.safeToInt()
        action.duration = newDuration
        isChanged = true
    }

    fun minutesEdit(minutes: String, action: Action = currAction) {
        val seconds = action.duration % 60
        action.duration = minutes.safeToInt() * 60 + seconds
        isChanged = true
    }

    fun newAction(index: Int) {
        val action = Action("", 60, 0, true, 0, setInfo.setId).apply {
            applyType(defaultTypes[1])
            name = context.getString(R.string.action) + " $index"
        }
        actions.add(action)
        updateActions()
        isChanged = true
    }


    // --------------------------------action editor methods-----------------------------------------------------------------------------
    fun applyActionType(actionType: ActionType): Action {
        if (actionType == defaultTypes[2]) {
            GlobalScope.launch {
                repository.insertActionType(ActionType(currAction.name, currAction.color))
            }
        } else {
            currAction.applyType(actionType)
        }
        isChanged = true
        return currAction
    }

    fun getActionTypes(): LiveData<List<ActionType>> {
        return repository.getActionTypes()
    }

    fun deleteActionType(actionType: ActionType) {
        for (i in defaultTypes) {
            if (actionType == i) return
        }
        GlobalScope.launch {
            repository.deleteActionType(actionType)
        }
    }

    fun switchTimeMode(boolean: Boolean) {
        currAction.exactTimeDefine = boolean
        isChanged = true
    }

    fun setActionName(name: String) {
        currAction.name = name
        isChanged = true
    }

    fun changeColor(newColor: Int) {
        currAction.color = newColor
    }

    fun copyAction() {
        actions.add(currAction.copy(actionId = 0))
    }

    fun deleteAction() {
        actions.remove(currAction)
        isChanged = true
    }

    private fun updateActions() {
        _actionsLiveData.value = actions.toList()
    }
}