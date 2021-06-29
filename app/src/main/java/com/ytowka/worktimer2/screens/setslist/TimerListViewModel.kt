package com.ytowka.worktimer2.screens.setslist

import androidx.lifecycle.*
import com.ytowka.worktimer2.data.Repository
import com.ytowka.worktimer2.data.models.ActionSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerListViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    var selectingMode = false
    var searchingMode = false

    private var setListLiveData: MutableLiveData<List<ActionSet>>? = null
    private var currentSortType = SortType.OPEN

    private val observer = Observer<List<ActionSet>>{ it ->
        setListLiveData!!.value = it.sortedByDescending { set ->
            set.setInfo.lastOpen
        }
    }

    fun getSetList(): LiveData<List<ActionSet>> {
        if(setListLiveData == null){
            setListLiveData = MutableLiveData()
            repository.getSets().observeForever(observer)
        }
        return setListLiveData!!
    }

    fun sort(): SortType{
        if (currentSortType == SortType.OPEN) {
            currentSortType = SortType.NAME
            setListLiveData?.let {
                it.value = it.value!!.sortedBy { set ->
                    set.setInfo.name
                }
            }
        } else if (currentSortType == SortType.NAME) {
            currentSortType = SortType.OPEN
            setListLiveData?.let {
                it.value = it.value!!.sortedByDescending { set ->
                    set.setInfo.lastOpen
                }
            }
        }
        return currentSortType
    }

    fun deleteSet(actionSet: ActionSet) {
        viewModelScope.launch {
            repository.deleteSet(actionSet)
        }
    }

    fun deleteSets(list: List<ActionSet>) {
        list.forEach {
            deleteSet(it)
        }
    }

    override fun onCleared() {
        repository.getSets().removeObserver(observer)
        super.onCleared()
    }

    enum class SortType {
        NAME,
        OPEN
    }
}