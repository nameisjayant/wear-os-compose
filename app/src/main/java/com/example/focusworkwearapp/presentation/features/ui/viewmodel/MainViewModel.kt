package com.example.focusworkwearapp.presentation.features.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusworkwearapp.presentation.common.doOnFailure
import com.example.focusworkwearapp.presentation.common.doOnLoading
import com.example.focusworkwearapp.presentation.common.doOnSuccess
import com.example.focusworkwearapp.presentation.data.models.Task
import com.example.focusworkwearapp.presentation.data.repository.MainRepository
import com.example.focusworkwearapp.presentation.data.repository.PreferenceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val preferenceStore: PreferenceStore
) : ViewModel() {
    private val _tasksResponse: MutableState<TaskStates> = mutableStateOf(TaskStates())
    val taskResponse: State<TaskStates> = _tasksResponse
    private val _task: MutableState<Task.TaskResponse> = mutableStateOf(Task.TaskResponse())
    val task: State<Task.TaskResponse> = _task

    fun setTaskData(data: Task.TaskResponse) {
        _task.value = data
    }

    init {
        viewModelScope.launch {
            repository.getTask()
                .doOnSuccess {
                    _tasksResponse.value = TaskStates(
                        data = it
                    )
                }.doOnFailure {
                    _tasksResponse.value = TaskStates(
                        msg = it?.message ?: "Something went wrong"
                    )
                }.doOnLoading {
                    _tasksResponse.value = TaskStates(
                        isLoading = true
                    )
                }.collect()
        }
    }

    fun setPref(key: Preferences.Key<Boolean>, value: Boolean) = viewModelScope.launch {
        preferenceStore.setBooleanPref(key, value)
    }

    fun setPref(key: Preferences.Key<Long>, value: Long) = viewModelScope.launch {
        preferenceStore.setLongPref(key, value)
    }

    fun getLongPref(key: Preferences.Key<Long>) = preferenceStore.getLongPref(key)

    fun getStringPref(key: Preferences.Key<String>) = preferenceStore.getStringPref(key)

    fun setStringPref(key: Preferences.Key<String>, value: String) = viewModelScope.launch {
        preferenceStore.setStringPref(key, value)
    }


    fun getBooleanPref(key: Preferences.Key<Boolean>) = preferenceStore.getBooleanPref(key)
    fun setBooleanPref(key: Preferences.Key<Boolean>, value: Boolean) = viewModelScope.launch {
        preferenceStore.setBooleanPref(key, value)
    }

}

data class TaskStates(
    val data: List<Task.TaskResponse> = emptyList(),
    val msg: String = "",
    val isLoading: Boolean = false
)