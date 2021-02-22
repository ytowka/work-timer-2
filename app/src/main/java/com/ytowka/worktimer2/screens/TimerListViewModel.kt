package com.ytowka.worktimer2.screens

import androidx.lifecycle.ViewModel
import com.ytowka.worktimer2.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@HiltViewModel
class TimerListViewModel @Inject constructor(repository: Repository) : ViewModel() {

}